package com.chillsam.courmy.main.presentation.chat

import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

/**
 * 채팅방 화면 ViewModel.
 *
 * 서버 연동 전 UI 단계라 대화 내용은 [ChatSampleData] 에서 방 id 로 찾아 채우고,
 * 전송한 메시지는 로컬 상태에만 쌓는다. 실제 메시지 API 가 붙으면 샘플 로딩·전송을 UseCase 로 바꾼다.
 */
@HiltViewModel
class ChatRoomViewModel
    @Inject
    constructor() :
    MviViewModel<ChatRoomIntent, ChatRoomUIState, ChatRoomReducerEvent>(ChatRoomUIState.empty) {
        override fun onIntent(intent: ChatRoomIntent) {
            when (intent) {
                is ChatRoomIntent.Load -> {
                    val room = ChatSampleData.find(intent.roomId)
                    dispatch(
                        ChatRoomReducerEvent.Loaded(
                            roomTitle = room?.name ?: "채팅",
                            subtitle = room?.subtitle.orEmpty(),
                            messages = room?.messages ?: persistentListOf(),
                        ),
                    )
                }

                is ChatRoomIntent.DraftChanged -> {
                    dispatch(ChatRoomReducerEvent.DraftUpdated(intent.value))
                }

                ChatRoomIntent.Send -> {
                    val text = currentState.draft.trim()
                    if (text.isEmpty()) return
                    val nextId = (currentState.messages.lastOrNull()?.id ?: 0L) + 1
                    dispatch(
                        ChatRoomReducerEvent.MessageSent(
                            ChatMessageUi(id = nextId, content = text, fromMe = true, time = "방금"),
                        ),
                    )
                }
            }
        }

        override fun reduce(
            state: ChatRoomUIState,
            event: ChatRoomReducerEvent,
        ): ChatRoomUIState =
            when (event) {
                is ChatRoomReducerEvent.Loaded -> {
                    state.copy(roomTitle = event.roomTitle, subtitle = event.subtitle, messages = event.messages)
                }

                is ChatRoomReducerEvent.DraftUpdated -> {
                    state.copy(draft = event.value)
                }

                is ChatRoomReducerEvent.MessageSent -> {
                    state.copy(
                        messages = (state.messages + event.message).toImmutableList(),
                        draft = "",
                    )
                }
            }
    }
