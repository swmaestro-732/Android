package com.chillsam.courmy.main.presentation.aichat

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 커미 AI 코스 추천 채팅 ViewModel.
 *
 * AI 백엔드 계약이 아직 없어, 정해진 질문 흐름(동행 → 동네 → 분위기 → 시간)을 따라가며
 * 수집한 답으로 코스 한 개를 제안하는 **스크립트 기반 목업**이다.
 * 실제 추천 API 가 생기면 이 클래스의 [askNext]·[deliverResult] 를 UseCase 호출로 교체한다.
 *
 * MVI 규칙을 그대로 지킨다 — 진입점은 [onIntent] 하나, 상태 변이는 [reduce] 에서만.
 * 답변 수집 상태([step]·[answers])는 화면에 노출하지 않는 내부 진행도라 ViewModel 필드로 둔다.
 */
@HiltViewModel
class AiCourseChatViewModel
    @Inject
    constructor() :
    MviViewModel<AiCourseChatIntent, AiCourseChatUIState, AiCourseChatReducerEvent>(
            AiCourseChatUIState.empty,
        ) {
        /** 지금까지 받은 답(질문 단계 → 답 라벨). 마지막 추천 문구를 만들 때 쓴다. */
        private val answers = linkedMapOf<Step, String>()

        /** 다음에 물어볼 질문 인덱스. [Step.entries] 크기와 같아지면 추천을 내보낸다. */
        private var step = 0

        /** 진행 중인 AI 응답 코루틴. 재시작·연타 시 취소해 중복 말풍선을 막는다. */
        private var replyJob: Job? = null

        private var messageId = 0L

        override fun onIntent(intent: AiCourseChatIntent) {
            when (intent) {
                AiCourseChatIntent.Start -> {
                    // 재진입(화면 복귀)마다 불려도 대화가 이미 있으면 다시 시작하지 않는다.
                    if (currentState.messages.isEmpty() && replyJob == null) askNext()
                }

                is AiCourseChatIntent.SendText -> {
                    val text = intent.text.trim()
                    if (text.isNotEmpty() && currentState.inputEnabled) handleAnswer(text)
                }

                is AiCourseChatIntent.QuickReply -> {
                    if (!currentState.inputEnabled) return
                    if (intent.value ==
                        RESTART_LABEL
                    ) {
                        onIntent(AiCourseChatIntent.Restart)
                    } else {
                        handleAnswer(intent.value)
                    }
                }

                AiCourseChatIntent.Restart -> {
                    restart()
                }
            }
        }

        override fun reduce(
            state: AiCourseChatUIState,
            event: AiCourseChatReducerEvent,
        ): AiCourseChatUIState =
            when (event) {
                is AiCourseChatReducerEvent.Append -> {
                    state.copy(messages = (state.messages + event.message).toImmutableList())
                }

                is AiCourseChatReducerEvent.SetTyping -> {
                    state.copy(isAiTyping = event.typing)
                }

                is AiCourseChatReducerEvent.SetQuickReplies -> {
                    state.copy(quickReplies = event.replies.toImmutableList())
                }

                is AiCourseChatReducerEvent.SetInputEnabled -> {
                    state.copy(inputEnabled = event.enabled)
                }

                AiCourseChatReducerEvent.Clear -> {
                    AiCourseChatUIState.empty
                }
            }

        /** 사용자 답 한 번을 처리한다 — 말풍선을 올리고 다음 질문 또는 추천으로 넘어간다. */
        private fun handleAnswer(text: String) {
            appendUser(text)

            // 추천까지 끝난 뒤의 자유 입력은 흐름을 더 진행하지 않고 안내만 한다.
            if (step >= Step.entries.size) {
                nudgeAfterResult()
                return
            }

            answers[Step.entries[step]] = text
            step++
            if (step < Step.entries.size) askNext() else deliverResult()
        }

        /** 현재 [step] 질문을 AI 말풍선 + 빠른 답변 칩으로 내보낸다(입력 중 표시 포함). */
        private fun askNext() {
            val question = SCRIPT.getValue(Step.entries[step])
            replyJob?.cancel()
            replyJob =
                viewModelScope.launch {
                    beginAiTurn()
                    delay(TYPING_DELAY_MS)
                    dispatch(AiCourseChatReducerEvent.SetTyping(false))
                    appendAi(question.prompt)
                    dispatch(AiCourseChatReducerEvent.SetQuickReplies(question.quickReplies))
                    dispatch(AiCourseChatReducerEvent.SetInputEnabled(true))
                }
        }

        /** 수집한 답으로 코스 추천 카드를 만들어 내보낸다. */
        private fun deliverResult() {
            replyJob?.cancel()
            replyJob =
                viewModelScope.launch {
                    beginAiTurn()
                    // 코스를 "고르는" 체감을 주려 질문 때보다 조금 더 오래 기다린다.
                    delay(RESULT_DELAY_MS)
                    dispatch(AiCourseChatReducerEvent.SetTyping(false))
                    appendAi(
                        text = RESULT_MESSAGE,
                        recommendation = buildRecommendation(),
                    )
                    dispatch(AiCourseChatReducerEvent.SetQuickReplies(listOf(RESTART_LABEL)))
                    dispatch(AiCourseChatReducerEvent.SetInputEnabled(true))
                }
        }

        /** 추천 이후 더 입력하면 "다시 추천받기"로 안내한다(흐름은 재진행하지 않는다). */
        private fun nudgeAfterResult() {
            replyJob?.cancel()
            replyJob =
                viewModelScope.launch {
                    beginAiTurn()
                    delay(TYPING_DELAY_MS)
                    dispatch(AiCourseChatReducerEvent.SetTyping(false))
                    appendAi("새로 추천받고 싶으면 아래 ‘$RESTART_LABEL’ 을 눌러 주세요.")
                    dispatch(AiCourseChatReducerEvent.SetQuickReplies(listOf(RESTART_LABEL)))
                    dispatch(AiCourseChatReducerEvent.SetInputEnabled(true))
                }
        }

        private fun restart() {
            replyJob?.cancel()
            replyJob = null
            answers.clear()
            step = 0
            messageId = 0L
            dispatch(AiCourseChatReducerEvent.Clear)
            askNext()
        }

        /** AI 턴 시작 공통 전처리 — 입력을 잠그고 칩을 치운 뒤 "입력 중" 을 켠다. */
        private fun beginAiTurn() {
            dispatch(AiCourseChatReducerEvent.SetInputEnabled(false))
            dispatch(AiCourseChatReducerEvent.SetQuickReplies(emptyList()))
            dispatch(AiCourseChatReducerEvent.SetTyping(true))
        }

        private fun appendUser(text: String) =
            dispatch(AiCourseChatReducerEvent.Append(AiChatMessage(messageId++, ChatRole.USER, text)))

        private fun appendAi(
            text: String,
            recommendation: AiCourseRecommendation? = null,
        ) = dispatch(
            AiCourseChatReducerEvent.Append(AiChatMessage(messageId++, ChatRole.AI, text, recommendation)),
        )

        /**
         * 수집한 답을 표시용 코스 요약으로 엮는다. 아는 값은 매핑하고, 자유 입력·미지정은 기본값으로 메운다.
         * (목업이라 실제 코스를 조회하지 않는다.)
         */
        private fun buildRecommendation(): AiCourseRecommendation {
            val region = REGION_LABEL[answers[Step.REGION]] ?: DEFAULT_REGION
            val companion = COMPANION_LABEL[answers[Step.COMPANION]] ?: DEFAULT_COMPANION
            val mood = answers[Step.MOOD]?.takeIf { it.isNotBlank() } ?: DEFAULT_MOOD
            val meta = TIME_META[answers[Step.TIME]] ?: DEFAULT_META
            return AiCourseRecommendation(
                tagLabel = "$region · $companion",
                title = "$region $mood 코스",
                meta = meta,
            )
        }

        /** 질문 흐름 단계. 선언 순서가 곧 질문 순서다. */
        private enum class Step { COMPANION, REGION, MOOD, TIME }

        private data class Question(
            val prompt: String,
            val quickReplies: List<String>,
        )

        private companion object {
            const val TYPING_DELAY_MS = 650L
            const val RESULT_DELAY_MS = 1200L

            const val RESTART_LABEL = "다시 추천받기"
            const val RESULT_MESSAGE = "이런 코스는 어때요? 마음에 들면 저장해 두세요."

            const val DEFAULT_REGION = "성수"
            const val DEFAULT_COMPANION = "나들이"
            const val DEFAULT_MOOD = "감성 카페"
            const val DEFAULT_META = "4 스팟 · 3시간"

            val SCRIPT =
                mapOf(
                    Step.COMPANION to
                        Question(
                            "안녕하세요! 코스 추천을 도와드릴 커미예요.\n오늘은 누구와 함께 떠나시나요?",
                            listOf("데이트", "친구와", "혼자", "가족과"),
                        ),
                    Step.REGION to
                        Question(
                            "좋아요! 어느 동네로 가볼까요?",
                            listOf("성수동", "연남동", "홍대", "아무데나 좋아요"),
                        ),
                    Step.MOOD to
                        Question(
                            "어떤 분위기를 원하세요?",
                            listOf("감성 카페", "맛집 탐방", "조용한 산책", "전시·갤러리"),
                        ),
                    Step.TIME to
                        Question(
                            "시간은 얼마나 쓸 수 있어요?",
                            listOf("2시간", "반나절", "하루 종일"),
                        ),
                )

            val REGION_LABEL =
                mapOf(
                    "성수동" to "성수",
                    "연남동" to "연남",
                    "홍대" to "홍대",
                    "아무데나 좋아요" to "성수",
                )

            val COMPANION_LABEL =
                mapOf(
                    "데이트" to "데이트",
                    "친구와" to "친구",
                    "혼자" to "혼자",
                    "가족과" to "가족",
                )

            val TIME_META =
                mapOf(
                    "2시간" to "3 스팟 · 2시간",
                    "반나절" to "4 스팟 · 4시간",
                    "하루 종일" to "6 스팟 · 하루",
                )
        }
    }
