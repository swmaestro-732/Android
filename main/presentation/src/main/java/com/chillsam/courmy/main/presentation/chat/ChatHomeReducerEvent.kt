package com.chillsam.courmy.main.presentation.chat

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import kotlinx.collections.immutable.ImmutableList

sealed interface ChatHomeReducerEvent : ReducerEvent {
    data class Loaded(
        val region: String,
        val conversations: ImmutableList<ConversationUi>,
        val neighborhoodRooms: ImmutableList<NeighborhoodRoomUi>,
    ) : ChatHomeReducerEvent

    data class TabSelected(
        val tab: ChatHomeTab,
    ) : ChatHomeReducerEvent
}
