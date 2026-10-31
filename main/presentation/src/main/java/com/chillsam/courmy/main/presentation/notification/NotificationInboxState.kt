package com.chillsam.courmy.main.presentation.notification

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.notification.NotificationItemVO
import com.chillsam.courmy.main.entity.notification.NotificationPageVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class NotificationInboxUIState(
    val items: ImmutableList<NotificationItemVO> = persistentListOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoadingMore: Boolean = false,
    val moreError: String? = null,
    val hasNext: Boolean = false,
    val nextCursor: Long? = null,
    val unreadCount: Long? = null,
    val isMarkingAll: Boolean = false,
) : UiState {
    fun withPage(
        page: NotificationPageVO,
        append: Boolean = false,
    ): NotificationInboxUIState =
        copy(
            items = ((if (append) items else emptyList()) + page.items).distinctBy { it.id }.toImmutableList(),
            isLoading = false,
            isLoadingMore = false,
            error = null,
            moreError = null,
            hasNext = page.hasNext,
            nextCursor = page.nextCursor,
        )

    fun withRead(id: Long): NotificationInboxUIState =
        copy(
            items = items.map { if (it.id == id) it.copy(isRead = true) else it }.toImmutableList(),
        )
}

sealed interface NotificationInboxIntent : MviIntent {
    data object Refresh : NotificationInboxIntent

    data object LoadMore : NotificationInboxIntent

    data object RefreshUnread : NotificationInboxIntent

    data class Open(
        val item: NotificationItemVO,
    ) : NotificationInboxIntent

    data object MarkAllRead : NotificationInboxIntent
}

sealed interface NotificationInboxReducerEvent : ReducerEvent {
    data object Loading : NotificationInboxReducerEvent

    data class Loaded(
        val page: NotificationPageVO,
    ) : NotificationInboxReducerEvent

    data class Failed(
        val message: String,
    ) : NotificationInboxReducerEvent

    data object LoadingMore : NotificationInboxReducerEvent

    data class MoreLoaded(
        val page: NotificationPageVO,
    ) : NotificationInboxReducerEvent

    data class MoreFailed(
        val message: String,
    ) : NotificationInboxReducerEvent

    data class UnreadLoaded(
        val count: Long?,
    ) : NotificationInboxReducerEvent

    data class Read(
        val id: Long,
    ) : NotificationInboxReducerEvent

    data object MarkingAll : NotificationInboxReducerEvent

    data object AllRead : NotificationInboxReducerEvent

    data object MarkAllFailed : NotificationInboxReducerEvent
}
