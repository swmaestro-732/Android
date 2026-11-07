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
 * AI 백엔드 계약이 아직 없어, 정해진 질문 흐름(지역 → 테마 → 특정 장소)을 따라가며
 * 수집한 답으로 코스 한 개를 제안하는 **스크립트 기반 목업**이다.
 * 실제 추천 API 가 생기면 이 클래스의 [askNext]·[deliverResult] 를 UseCase 호출로 교체한다.
 *
 * 장소 질문은 분기한다 — "없어요"면 바로 추천으로 가고, "네, 있어요"면 장소 이름을 한 번 더 받는다.
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

        /** "네, 있어요"를 고른 뒤 장소 이름을 입력으로 기다리는 중인지. */
        private var awaitingPlaceDetail = false

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

                is AiCourseChatIntent.SetLayout -> {
                    dispatch(AiCourseChatReducerEvent.SetLayout(intent.layout))
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
                    state.copy(quickReplies = event.replies.toImmutableList(), quickReplyKind = event.kind)
                }

                is AiCourseChatReducerEvent.SetInputEnabled -> {
                    state.copy(inputEnabled = event.enabled)
                }

                AiCourseChatReducerEvent.Clear -> {
                    AiCourseChatUIState.empty.copy(resultLayout = state.resultLayout)
                }

                is AiCourseChatReducerEvent.SetLayout -> {
                    state.copy(resultLayout = event.layout)
                }
            }

        /** 사용자 답 한 번을 처리한다 — 말풍선을 올리고 다음 질문·장소 입력·추천으로 분기한다. */
        private fun handleAnswer(text: String) {
            appendUser(text)

            // 추천까지 끝난 뒤의 자유 입력은 흐름을 더 진행하지 않고 안내만 한다.
            if (step >= Step.entries.size) {
                nudgeAfterResult()
                return
            }

            // "네, 있어요" 뒤 장소 이름을 받는 중이면, 그 입력을 장소로 담고 추천으로 간다.
            if (awaitingPlaceDetail) {
                answers[Step.PLACE] = text
                awaitingPlaceDetail = false
                finishFlow()
                return
            }

            when (Step.entries[step]) {
                Step.PLACE -> {
                    when (text) {
                        PLACE_SKIP -> {
                            finishFlow()
                        }

                        // "없어요" → 장소 없이 바로 추천
                        PLACE_YES -> {
                            askPlaceDetail()
                        }

                        // "네, 있어요" → 장소 이름을 한 번 더 묻는다
                        else -> {
                            // 칩 대신 자유 입력으로 장소를 바로 적은 경우.
                            answers[Step.PLACE] = text
                            finishFlow()
                        }
                    }
                }

                else -> {
                    answers[Step.entries[step]] = text
                    step++
                    askNext()
                }
            }
        }

        /** 흐름을 끝으로 보내고 추천을 내보낸다. */
        private fun finishFlow() {
            step = Step.entries.size
            deliverResult()
        }

        /** 현재 [step] 질문을 AI 말풍선 + 빠른 답변 카드로 내보낸다(입력 중 표시 포함). */
        private fun askNext() {
            val current = Step.entries[step]
            val question = SCRIPT.getValue(current)
            replyJob?.cancel()
            replyJob =
                viewModelScope.launch {
                    beginAiTurn()
                    delay(TYPING_DELAY_MS)
                    dispatch(AiCourseChatReducerEvent.SetTyping(false))
                    appendAi(question.prompt)
                    dispatch(AiCourseChatReducerEvent.SetQuickReplies(question.quickReplies, current.quickReplyKind))
                    dispatch(AiCourseChatReducerEvent.SetInputEnabled(true))
                }
        }

        /** "네, 있어요" 뒤 장소 이름을 직접 입력받는다(카드 없이 입력창으로). */
        private fun askPlaceDetail() {
            awaitingPlaceDetail = true
            replyJob?.cancel()
            replyJob =
                viewModelScope.launch {
                    beginAiTurn()
                    delay(TYPING_DELAY_MS)
                    dispatch(AiCourseChatReducerEvent.SetTyping(false))
                    appendAi(PLACE_DETAIL_PROMPT)
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
                        recommendations = buildRecommendations(),
                    )
                    dispatch(AiCourseChatReducerEvent.SetQuickReplies(listOf(RESTART_LABEL), QuickReplyKind.GENERIC))
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
                    dispatch(AiCourseChatReducerEvent.SetQuickReplies(listOf(RESTART_LABEL), QuickReplyKind.GENERIC))
                    dispatch(AiCourseChatReducerEvent.SetInputEnabled(true))
                }
        }

        private fun restart() {
            replyJob?.cancel()
            replyJob = null
            answers.clear()
            step = 0
            awaitingPlaceDetail = false
            messageId = 0L
            dispatch(AiCourseChatReducerEvent.Clear)
            askNext()
        }

        /** AI 턴 시작 공통 전처리 — 입력을 잠그고 카드를 치운 뒤 "입력 중" 을 켠다. */
        private fun beginAiTurn() {
            dispatch(AiCourseChatReducerEvent.SetInputEnabled(false))
            dispatch(AiCourseChatReducerEvent.SetQuickReplies(emptyList()))
            dispatch(AiCourseChatReducerEvent.SetTyping(true))
        }

        private fun appendUser(text: String) =
            dispatch(AiCourseChatReducerEvent.Append(AiChatMessage(messageId++, ChatRole.USER, text)))

        private fun appendAi(
            text: String,
            recommendations: List<AiCourseRecommendation> = emptyList(),
        ) = dispatch(
            AiCourseChatReducerEvent.Append(AiChatMessage(messageId++, ChatRole.AI, text, recommendations)),
        )

        /**
         * 수집한 답으로 추천 코스 3개를 엮는다. 첫 번째는 고른 테마 그대로, 나머지 둘은 결이 다른 제안을 섞는다.
         * 특정 장소를 담았으면 첫 코스 제목에 녹인다. (목업이라 실제 코스를 조회하지 않는다.)
         */
        private fun buildRecommendations(): List<AiCourseRecommendation> {
            val region =
                REGION_LABEL[answers[Step.REGION]] ?: answers[Step.REGION]?.takeIf { it.isNotBlank() } ?: DEFAULT_REGION
            val theme = answers[Step.THEME]?.takeIf { it.isNotBlank() && it != THEME_ANY } ?: DEFAULT_THEME
            val place = answers[Step.PLACE]?.takeIf { it.isNotBlank() }
            val primaryTitle = if (place != null) "$place 들르는 $region $theme 코스" else "$region $theme 코스"
            return listOf(
                AiCourseRecommendation("$region · $theme", primaryTitle, "4 스팟 · 3시간"),
                AiCourseRecommendation("$region · 산책", "$region 골목 산책 코스", "3 스팟 · 2시간"),
                AiCourseRecommendation("$region · 전시", "$region 갤러리 투어 코스", "5 스팟 · 4시간"),
            )
        }

        /** 질문 흐름 단계. 선언 순서가 곧 질문 순서다. */
        private enum class Step(
            val quickReplyKind: QuickReplyKind,
        ) {
            REGION(QuickReplyKind.REGION),
            THEME(QuickReplyKind.THEME),
            PLACE(QuickReplyKind.PLACE),
        }

        private data class Question(
            val prompt: String,
            val quickReplies: List<String>,
        )

        private companion object {
            const val TYPING_DELAY_MS = 650L
            const val RESULT_DELAY_MS = 1200L

            const val RESTART_LABEL = "다시 추천받기"
            const val RESULT_MESSAGE = "취향에 맞춰 3개 골라봤어요.\n마음에 드는 코스를 저장해 두세요."
            const val PLACE_DETAIL_PROMPT = "어떤 장소를 담아둘까요? 이름을 알려주세요."

            // 장소 질문의 두 선택지 — 각각 바로 추천 / 장소 입력으로 분기한다.
            const val PLACE_YES = "네, 있어요"
            const val PLACE_SKIP = "없어요 (바로 추천받기)"
            const val THEME_ANY = "상관없어요"

            const val DEFAULT_REGION = "성수"
            const val DEFAULT_THEME = "감성 카페"
            const val DEFAULT_META = "4 스팟 · 3시간"

            val SCRIPT =
                mapOf(
                    Step.REGION to
                        Question(
                            "안녕하세요, 커미 AI예요!\n먼저, 어느 지역으로 떠나볼까요?",
                            listOf("내 근처", "성수", "연남", "홍대"),
                        ),
                    Step.THEME to
                        Question(
                            "원하는 코스 테마가 있나요?",
                            listOf("데이트", "감성 카페", "맛집 탐방", "조용한 산책", THEME_ANY),
                        ),
                    Step.PLACE to
                        Question(
                            "특별히 가고 싶은 장소가 있나요?",
                            listOf(PLACE_YES, PLACE_SKIP),
                        ),
                )

            val REGION_LABEL =
                mapOf(
                    "내 근처" to "내 주변",
                    "성수" to "성수",
                    "연남" to "연남",
                    "홍대" to "홍대",
                )
        }
    }
