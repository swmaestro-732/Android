package com.chillsam.courmy.main.presentation.notification

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.helper.MessageHelper
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.push.NotificationEvents
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.notification.NotificationRepository
import com.chillsam.courmy.main.entity.notification.NotificationItemVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class NotificationInboxViewModel
    @Inject
    constructor(
        private val repository: NotificationRepository,
        private val navigation: NavigationHelper,
        private val messages: MessageHelper,
        events: NotificationEvents,
    ) : MviViewModel<NotificationInboxIntent, NotificationInboxUIState, NotificationInboxReducerEvent>(
            NotificationInboxUIState(),
        ) {
        // 목록 응답이 읽음 변경을 되돌리지 않도록 조회와 변경을 한 순서로 처리한다.
        private val operations = Mutex()
        private var refreshJob: Job? = null
        private var unreadJob: Job? = null
        private val opening = mutableSetOf<Long>()
        private var listRequested = false

        init {
            viewModelScope.launch {
                events.changes.collect {
                    // 수신 직전 시작한 응답이 새 알림을 포함하지 않을 수 있어 진행 중 조회 뒤에 다시 읽는다.
                    refreshJob?.join()
                    unreadJob?.join()
                    if (listRequested) refresh() else refreshUnread()
                }
            }
        }

        override fun onIntent(intent: NotificationInboxIntent) {
            when (intent) {
                NotificationInboxIntent.Refresh -> refresh()
                NotificationInboxIntent.LoadMore -> loadMore()
                NotificationInboxIntent.RefreshUnread -> refreshUnread()
                is NotificationInboxIntent.Open -> open(intent.item)
                NotificationInboxIntent.MarkAllRead -> markAll()
            }
        }

        override fun reduce(
            state: NotificationInboxUIState,
            event: NotificationInboxReducerEvent,
        ): NotificationInboxUIState =
            when (event) {
                NotificationInboxReducerEvent.Loading -> {
                    state.copy(isLoading = true, error = null, moreError = null)
                }

                is NotificationInboxReducerEvent.Loaded -> {
                    state.withPage(event.page)
                }

                is NotificationInboxReducerEvent.Failed -> {
                    state.copy(isLoading = false, error = event.message)
                }

                NotificationInboxReducerEvent.LoadingMore -> {
                    state.copy(isLoadingMore = true, moreError = null)
                }

                is NotificationInboxReducerEvent.MoreLoaded -> {
                    state.withPage(event.page, append = true)
                }

                is NotificationInboxReducerEvent.MoreFailed -> {
                    state.copy(isLoadingMore = false, moreError = event.message)
                }

                is NotificationInboxReducerEvent.UnreadLoaded -> {
                    state.copy(unreadCount = event.count)
                }

                is NotificationInboxReducerEvent.Read -> {
                    state.withRead(event.id)
                }

                NotificationInboxReducerEvent.MarkingAll -> {
                    state.copy(isMarkingAll = true)
                }

                NotificationInboxReducerEvent.AllRead -> {
                    state.copy(
                        items = state.items.map { it.copy(isRead = true) }.toImmutableList(),
                        isMarkingAll = false,
                    )
                }

                NotificationInboxReducerEvent.MarkAllFailed -> {
                    state.copy(isMarkingAll = false)
                }
            }

        private fun refresh() {
            listRequested = true
            if (refreshJob?.isActive == true) return
            refreshJob =
                viewModelScope.launch {
                    operations.withLock {
                        dispatch(NotificationInboxReducerEvent.Loading)
                        runCatching { repository.getNotifications() }
                            .onSuccess { dispatch(NotificationInboxReducerEvent.Loaded(it)) }
                            .onFailure {
                                rethrowCancellation(it)
                                dispatch(NotificationInboxReducerEvent.Failed("알림을 불러오지 못했어요. 다시 시도해 주세요."))
                            }
                        loadUnread()
                    }
                }
        }

        private fun loadMore() {
            val state = currentState
            if (state.isLoading || state.isLoadingMore || !state.hasNext) return
            val cursor = state.nextCursor ?: return
            dispatch(NotificationInboxReducerEvent.LoadingMore)
            viewModelScope.launch {
                operations.withLock {
                    // 대기 중 새로고침이 커서를 바꿨으면 이전 페이지 요청은 버린다.
                    if (currentState.nextCursor != cursor) {
                        dispatch(NotificationInboxReducerEvent.MoreFailed("목록이 변경되었어요. 다시 불러와 주세요."))
                    } else {
                        runCatching { repository.getNotifications(cursor) }
                            .onSuccess { dispatch(NotificationInboxReducerEvent.MoreLoaded(it)) }
                            .onFailure {
                                rethrowCancellation(it)
                                dispatch(NotificationInboxReducerEvent.MoreFailed("다음 알림을 불러오지 못했어요."))
                            }
                    }
                }
            }
        }

        private fun refreshUnread() {
            if (unreadJob?.isActive == true) return
            unreadJob = viewModelScope.launch { operations.withLock { loadUnread() } }
        }

        private suspend fun loadUnread() {
            runCatching { repository.unreadCount() }
                .onSuccess { dispatch(NotificationInboxReducerEvent.UnreadLoaded(it)) }
                .onFailure {
                    rethrowCancellation(it)
                    // 실패를 0개로 표시하지 않는다.
                    dispatch(NotificationInboxReducerEvent.UnreadLoaded(null))
                }
        }

        private fun open(item: NotificationItemVO) {
            if (!opening.add(item.id)) return
            if (item.targetType == "COURSE" && item.targetId > 0) {
                navigation.navigateDeepLink(CourseDetailPage.route(item.targetId.toString()))
            }
            viewModelScope.launch {
                try {
                    operations.withLock {
                        val current = currentState.items.firstOrNull { it.id == item.id } ?: item
                        if (!current.isRead) {
                            runCatching { repository.markRead(item.id) }
                                .onSuccess { dispatch(NotificationInboxReducerEvent.Read(item.id)) }
                                .onFailure {
                                    rethrowCancellation(it)
                                    messages.showToast("읽음 상태를 저장하지 못했어요.")
                                }
                            loadUnread()
                        }
                    }
                } finally {
                    opening.remove(item.id)
                }
            }
        }

        private fun markAll() {
            if (currentState.isMarkingAll) return
            dispatch(NotificationInboxReducerEvent.MarkingAll)
            viewModelScope.launch {
                operations.withLock {
                    runCatching { repository.markAllRead() }
                        .onSuccess { dispatch(NotificationInboxReducerEvent.AllRead) }
                        .onFailure {
                            rethrowCancellation(it)
                            dispatch(NotificationInboxReducerEvent.MarkAllFailed)
                            messages.showToast("읽음 상태를 저장하지 못했어요.")
                        }
                    // read-all의 count는 변경한 행 수다. 남은 미읽음 수로 사용하지 않는다.
                    loadUnread()
                }
            }
        }

        private fun rethrowCancellation(failure: Throwable) {
            if (failure is CancellationException) throw failure
        }
    }
