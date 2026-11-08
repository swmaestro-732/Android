package com.chillsam.courmy.main.presentation.aichat

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.aichat.SendAiChatMessageUseCase
import com.chillsam.courmy.main.entity.aichat.AiChatAnswer
import com.chillsam.courmy.main.entity.aichat.AiChatAnswerType
import com.chillsam.courmy.main.entity.aichat.AiChatStep
import com.chillsam.courmy.main.entity.aichat.AiChatTurnVO
import com.chillsam.courmy.main.entity.aichat.LatLng
import com.chillsam.courmy.main.entity.aichat.QuickReplyVO
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 커미 AI 채팅 ViewModel. 서버 주도 대화(`POST /ai-chat/messages`)를 턴 단위로 진행한다.
 *
 * REGION(인사말·지역 질문·지역 칩)은 앱이 소유하고, 그 뒤 문구·칩·입력창·코스는 서버 턴을 그대로 그린다.
 * 단계는 응답 [AiChatStep] 으로만 분기하며 순서를 가정하지 않는다(서버가 건너뛸 수 있음).
 * 세션은 서버가 응답으로 준 sessionId 를 이어 보내고, "처음부터"(RESTART)·세션 만료(4400)는 앱이 첫 화면으로 되돌린다.
 */
@HiltViewModel
class AiCourseChatViewModel
    @Inject
    constructor(
        @ApplicationContext private val appContext: Context,
        private val sendMessage: SendAiChatMessageUseCase,
    ) : MviViewModel<AiCourseChatIntent, AiCourseChatUIState, AiCourseChatReducerEvent>(
            AiCourseChatUIState.empty,
        ) {
        private var sessionId: String? = null
        private var replyJob: Job? = null
        private var messageId = 0L

        /** 화면이 전달한 현재 좌표(권한 없으면 null). 내 근처·requiresLocation 칩에 싣는다. */
        private var location: LatLng? = null

        /** 역지오코딩으로 뽑은 근처 지역 이름(동·구). 지역 칩은 "내 근처" + 이 목록. */
        private var regionNames: List<String> = emptyList()

        override fun onIntent(intent: AiCourseChatIntent) {
            when (intent) {
                AiCourseChatIntent.Start -> {
                    if (currentState.messages.isEmpty() && replyJob == null) showGreeting()
                }

                is AiCourseChatIntent.ProvideLocation -> {
                    onLocation(intent.latitude, intent.longitude)
                }

                is AiCourseChatIntent.RegionChipTap -> {
                    onRegionChip(intent.label)
                }

                is AiCourseChatIntent.QuickReplyTap -> {
                    onQuickReply(intent.reply)
                }

                is AiCourseChatIntent.SendText -> {
                    val text = intent.text.trim()
                    if (text.isNotEmpty() && currentState.inputEnabled) {
                        appendUser(text)
                        send(AiChatAnswer(sessionId, AiChatAnswerType.TEXT, text = text))
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

                is AiCourseChatReducerEvent.SetInput -> {
                    state.copy(
                        inputEnabled = event.enabled,
                        inputPlaceholder = event.placeholder.ifBlank { AiCourseChatUIState.DEFAULT_PLACEHOLDER },
                    )
                }

                is AiCourseChatReducerEvent.SetStep -> {
                    state.copy(step = event.step)
                }

                is AiCourseChatReducerEvent.SetRegionChips -> {
                    state.copy(regionChips = event.chips.toImmutableList(), showRegionChips = event.show)
                }

                AiCourseChatReducerEvent.Reset -> {
                    AiCourseChatUIState.empty
                }
            }

        /** 앱 소유 첫 화면 — 인사말 + 지역 질문 + 지역 칩(현재 위치 기준). */
        private fun showGreeting() {
            appendAi(GREETING)
            dispatch(AiCourseChatReducerEvent.SetStep(AiChatStep.REGION))
            dispatch(AiCourseChatReducerEvent.SetInput(enabled = true, placeholder = REGION_PLACEHOLDER))
            dispatch(AiCourseChatReducerEvent.SetQuickReplies(emptyList()))
            dispatch(AiCourseChatReducerEvent.SetRegionChips(regionChips(), show = true))
        }

        private fun onLocation(
            lat: Double,
            lng: Double,
        ) {
            location = LatLng(lat, lng)
            // 첫 화면(REGION)일 때만 역지오코딩해 지역 칩을 채운다.
            if (currentState.step == AiChatStep.REGION && regionNames.isEmpty()) {
                viewModelScope.launch {
                    regionNames = reverseGeocodeRegionNames(appContext, lat, lng)
                    if (currentState.showRegionChips) {
                        dispatch(AiCourseChatReducerEvent.SetRegionChips(regionChips(), show = true))
                    }
                }
            }
        }

        private fun onRegionChip(label: String) {
            if (!currentState.inputEnabled) return
            appendUser(label)
            val answer =
                if (label == NEAR_ME_LABEL) {
                    AiChatAnswer(sessionId, AiChatAnswerType.CHIP, value = NEAR_ME_VALUE, location = location)
                } else {
                    AiChatAnswer(sessionId, AiChatAnswerType.TEXT, text = label)
                }
            send(answer)
        }

        private fun onQuickReply(reply: QuickReplyVO) {
            if (!currentState.inputEnabled) return
            if (reply.value == QuickReplyVO.VALUE_RESTART) {
                restart()
                return
            }
            appendUser(reply.label)
            send(
                AiChatAnswer(
                    sessionId = sessionId,
                    type = AiChatAnswerType.CHIP,
                    value = reply.value,
                    location = if (reply.requiresLocation) location else null,
                ),
            )
        }

        /** 답변을 보내고 다음 턴을 반영한다. */
        private fun send(answer: AiChatAnswer) {
            replyJob?.cancel()
            replyJob =
                viewModelScope.launch {
                    beginTurn()
                    runCatching { sendMessage(answer) }
                        .onSuccess { applyTurn(it) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            handleError(e)
                        }
                }
        }

        /** 서버 턴을 화면에 반영한다. */
        private fun applyTurn(turn: AiChatTurnVO) {
            sessionId = turn.sessionId.takeIf { it.isNotBlank() } ?: sessionId
            dispatch(AiCourseChatReducerEvent.SetTyping(false))

            val lastIndex = turn.messages.lastIndex
            turn.messages.forEachIndexed { index, text ->
                // 코스는 마지막 말풍선에만 붙인다(RESULT 턴).
                appendAi(text, if (index == lastIndex) turn.courses else emptyList())
            }

            dispatch(AiCourseChatReducerEvent.SetStep(turn.step))
            dispatch(AiCourseChatReducerEvent.SetQuickReplies(turn.quickReplies))
            dispatch(AiCourseChatReducerEvent.SetInput(turn.input.enabled, turn.input.placeholder))

            // REGION 재질문(서버 칩 없음)이면 앱 지역 칩을 다시 보여 준다.
            val showRegion = turn.step == AiChatStep.REGION && turn.quickReplies.isEmpty()
            dispatch(AiCourseChatReducerEvent.SetRegionChips(regionChips(), show = showRegion))
        }

        private fun handleError(e: Throwable) {
            dispatch(AiCourseChatReducerEvent.SetTyping(false))
            // 4400(세션 만료)은 HTTP 404 로 온다 — 첫 화면으로 되돌린다.
            if (e is HttpResponseException && e.rawCode == HTTP_NOT_FOUND) {
                Log.i(TAG, "세션 만료(4400) — 처음부터")
                restart(SESSION_EXPIRED_NOTICE)
                return
            }
            Log.w(TAG, "커미 AI 전송 실패", e)
            appendAi(ERROR_NOTICE)
            dispatch(AiCourseChatReducerEvent.SetInput(enabled = true, placeholder = currentState.inputPlaceholder))
        }

        /** 턴 시작 — 입력 잠금 + "입력 중". 칩은 그대로 두되 입력 비활성으로 눌리지 않게 한다. */
        private fun beginTurn() {
            dispatch(AiCourseChatReducerEvent.SetInput(enabled = false, placeholder = currentState.inputPlaceholder))
            dispatch(AiCourseChatReducerEvent.SetTyping(true))
        }

        private fun restart(notice: String? = null) {
            replyJob?.cancel()
            replyJob = null
            sessionId = null
            messageId = 0L
            dispatch(AiCourseChatReducerEvent.Reset)
            notice?.let { appendAi(it) }
            showGreeting()
        }

        private fun regionChips(): List<String> = listOf(NEAR_ME_LABEL) + regionNames

        private fun appendUser(text: String) =
            dispatch(AiCourseChatReducerEvent.Append(AiChatMessage(messageId++, ChatRole.USER, text)))

        private fun appendAi(
            text: String,
            courses: List<com.chillsam.courmy.main.entity.home.HomeCourseVO> = emptyList(),
        ) = dispatch(
            AiCourseChatReducerEvent.Append(
                AiChatMessage(messageId++, ChatRole.AI, text, courses.toImmutableList()),
            ),
        )

        private companion object {
            const val TAG = "AiChat"
            const val HTTP_NOT_FOUND = 404

            const val GREETING = "안녕하세요, 커미 AI예요!\n먼저, 어느 지역으로 떠나볼까요?"
            const val REGION_PLACEHOLDER = "지역을 입력해도 돼요"
            const val SESSION_EXPIRED_NOTICE = "대화가 만료돼서 처음부터 다시 시작할게요."
            const val ERROR_NOTICE = "지금은 추천을 불러오지 못했어요. 잠시 후 다시 시도해 주세요."
        }
    }
