package com.chillsam.courmy.main.presentation.chat

import com.chillsam.courmy.common.domain.helper.ResourceHelper
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

/**
 * 채팅홈(참여 중 · 동네 채팅방) ViewModel.
 *
 * 서버 연동 전 UI 단계라 두 목록 모두 [ChatSampleData] 에서 만든다.
 * 참여 중 줄의 미리보기 문구·시간은 방의 미리보기 값을 쓴다. 실제 API 가 붙으면 Load 를 UseCase 로 바꾼다.
 */
@HiltViewModel
class ChatHomeViewModel
    @Inject
    constructor(
        private val resources: ResourceHelper,
    ) : MviViewModel<ChatHomeIntent, ChatHomeUIState, ChatHomeReducerEvent>(ChatHomeUIState.empty) {
        override fun onIntent(intent: ChatHomeIntent) {
            when (intent) {
                ChatHomeIntent.Load -> {
                    dispatch(
                        ChatHomeReducerEvent.Loaded(
                            region = ChatSampleData.REGION,
                            conversations = buildConversations(),
                            neighborhoodRooms = buildNeighborhoodRooms(),
                        ),
                    )
                }

                is ChatHomeIntent.SelectTab -> {
                    dispatch(ChatHomeReducerEvent.TabSelected(intent.tab))
                }
            }
        }

        override fun reduce(
            state: ChatHomeUIState,
            event: ChatHomeReducerEvent,
        ): ChatHomeUIState =
            when (event) {
                is ChatHomeReducerEvent.Loaded -> {
                    state.copy(
                        region = event.region,
                        conversations = event.conversations,
                        neighborhoodRooms = event.neighborhoodRooms,
                    )
                }

                is ChatHomeReducerEvent.TabSelected -> {
                    state.copy(selectedTab = event.tab)
                }
            }

        private fun buildConversations() =
            ChatSampleData
                .participating()
                .map { room ->
                    ConversationUi(
                        roomId = room.id,
                        name = room.name,
                        regionTag = room.regionTag,
                        lastMessage = room.previewMessage,
                        time = room.previewTime,
                        unreadCount = room.unreadCount,
                    )
                }.toImmutableList()

        private fun buildNeighborhoodRooms() =
            ChatSampleData.neighborhoodRooms
                .map { room ->
                    val memberText = resources.getString(StringResource.CHAT_ACTIVE_MEMBER_COUNT, room.memberCount)
                    NeighborhoodRoomUi(
                        roomId = room.roomId,
                        name = room.name,
                        subtitle =
                            if (room.openToEveryone) {
                                "${resources.getString(
                                    StringResource.CHAT_OPEN_TO_EVERYONE,
                                )} · $memberText"
                            } else {
                                memberText
                            },
                        featured = room.featured,
                        initial = room.name.take(1),
                        joined = room.joined,
                    )
                }.toImmutableList()
    }
