package com.chillsam.courmy.main.presentation.chat

import androidx.compose.runtime.Immutable
import com.chillsam.courmy.common.presentation.mvi.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** 채팅홈 상단 세그먼트 탭. */
enum class ChatHomeTab {
    /** 참여 중(가입한 방·대화). */
    PARTICIPATING,

    /** 우리 동네 채팅방. */
    NEIGHBORHOOD,
}

/** 참여 중 목록의 한 줄. */
@Immutable
data class ConversationUi(
    val roomId: Long,
    val name: String,
    val regionTag: String?,
    val lastMessage: String,
    val time: ChatTime,
    val unreadCount: Int,
)

/** 동네 채팅방 목록의 한 줄. */
@Immutable
data class NeighborhoodRoomUi(
    val roomId: Long,
    val name: String,
    val subtitle: String,
    val featured: Boolean,
    val initial: String,
    val joined: Boolean,
)

/** 채팅홈(참여 중 · 동네 채팅방) 화면 상태. */
data class ChatHomeUIState(
    val region: String = "",
    val selectedTab: ChatHomeTab = ChatHomeTab.PARTICIPATING,
    val conversations: ImmutableList<ConversationUi> = persistentListOf(),
    val neighborhoodRooms: ImmutableList<NeighborhoodRoomUi> = persistentListOf(),
) : UiState {
    companion object {
        val empty: ChatHomeUIState = ChatHomeUIState()
    }
}
