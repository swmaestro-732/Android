package com.chillsam.courmy.main.presentation.chat

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import kotlinx.collections.immutable.ImmutableList

sealed interface ChatRoomReducerEvent : ReducerEvent {
    data class Loaded(
        val roomTitle: String,
        val subtitle: String,
        val messages: ImmutableList<ChatMessageUi>,
    ) : ChatRoomReducerEvent

    data class DraftUpdated(
        val value: String,
    ) : ChatRoomReducerEvent

    data class MessageSent(
        val message: ChatMessageUi,
    ) : ChatRoomReducerEvent
}
