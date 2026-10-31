package com.chillsam.courmy.main.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.SkeletonBlock
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.component.BackTopBar

@Composable
fun NotificationInboxPage(viewModel: NotificationInboxViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navigation = LocalNavigationHelper.current
    RefreshOnResume { viewModel.onIntent(NotificationInboxIntent.Refresh) }
    NotificationInboxContent(state, viewModel::onIntent, navigation::navigateToBack)
}

@Composable
fun NotificationInboxContent(
    state: NotificationInboxUIState,
    onIntent: (NotificationInboxIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    StatusBarColor(color.bgDefaultLevel0)
    Column(modifier.fillMaxSize().background(color.bgDefaultLevel0).navigationBarsPadding()) {
        BackTopBar("알림", onBack, boxed = true)
        InboxSummary(state, onIntent)
        when {
            state.isLoading && state.items.isEmpty() -> {
                InboxLoading()
            }

            state.error != null && state.items.isEmpty() -> {
                InboxEmpty(
                    title = "알림을 불러오지 못했어요",
                    description = state.error,
                    action = "다시 시도",
                    onAction = { onIntent(NotificationInboxIntent.Refresh) },
                )
            }

            state.items.isEmpty() -> {
                InboxEmpty(
                    title = "아직 도착한 알림이 없어요",
                    description = "내 코스에 남겨진 반응과\n새로운 팔로우 소식을 여기서 확인해요.",
                )
            }

            else -> {
                InboxList(state, onIntent)
            }
        }
    }
}

@Composable
private fun InboxSummary(
    state: NotificationInboxUIState,
    onIntent: (NotificationInboxIntent) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        Modifier.fillMaxWidth().padding(start = 24.dp, end = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DsText(
            text = state.unreadCount?.let { if (it > 0) "안 읽은 알림 ${it}개" else "모든 알림을 읽었어요" } ?: "최근 알림",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
        )
        TextButton(
            onClick = { onIntent(NotificationInboxIntent.MarkAllRead) },
            enabled = !state.isMarkingAll && (state.unreadCount ?: 0) > 0,
        ) {
            DsText(
                if (state.isMarkingAll) "읽는 중…" else "모두 읽음",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = if ((state.unreadCount ?: 0) > 0) color.contentAccent else color.contentDefaultLevel3,
            )
        }
    }
}

@Composable
private fun InboxList(
    state: NotificationInboxUIState,
    onIntent: (NotificationInboxIntent) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
        items(state.items, key = { it.id }) { item ->
            NotificationRow(item, onClick = { onIntent(NotificationInboxIntent.Open(item)) })
        }
        item {
            if (state.error != null) {
                InboxRetry(state.error, "새로고침", { onIntent(NotificationInboxIntent.Refresh) })
            }
            when {
                state.isLoadingMore -> {
                    SkeletonBlock(Modifier.fillMaxWidth().padding(24.dp).height(48.dp))
                }

                state.moreError != null -> {
                    InboxRetry(
                        state.moreError,
                        "다시 시도",
                        { onIntent(NotificationInboxIntent.LoadMore) },
                    )
                }

                state.hasNext -> {
                    InboxRetry(null, "이전 알림 더 보기", { onIntent(NotificationInboxIntent.LoadMore) })
                }
            }
        }
    }
}

@Composable
private fun InboxRetry(
    message: String?,
    action: String,
    onAction: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (message != null) DsText(message, style = DesignSystemThemeImpl.typeScale.textRegularXS)
        TextButton(onClick = onAction) { DsText(action, style = DesignSystemThemeImpl.typeScale.textStrongS) }
    }
}

@Composable
private fun InboxLoading() {
    Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(5) { SkeletonBlock(Modifier.fillMaxWidth().height(88.dp)) }
    }
}
