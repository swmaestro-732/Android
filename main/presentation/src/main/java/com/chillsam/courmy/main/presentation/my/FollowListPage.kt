package com.chillsam.courmy.main.presentation.my

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.domain.user.UserProfilePage
import com.chillsam.courmy.main.entity.my.FollowUserVO
import com.chillsam.courmy.main.presentation.profile.ProfileAvatar
import com.chillsam.courmy.main.domain.my.MyPage as MyRoute

/**
 * 팔로우 목록 화면(FS-15 O). 팔로워/팔로잉 탭 전환 + 사용자 목록(제거 ✕).
 * 프로필의 팔로워/팔로잉을 눌러 [initialTab] 으로 진입한다.
 * [targetUserId] 가 null 이면 내 목록, 값이 있으면 그 사용자의 목록이다.
 */
@Composable
fun FollowListPage(
    initialTab: FollowTab,
    targetUserId: Long? = null,
    modifier: Modifier = Modifier,
    viewModel: FollowListViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab = uiState.selectedTab
    val users = uiState.users
    var pendingRemoval by remember { mutableStateOf<FollowUserVO?>(null) }
    // 대상이 지정되지 않았으면 내 목록이다(라우트 인자 없이 마이에서 진입).
    val isMyList = targetUserId == null
    // 해제할 수 있는 건 "내가 팔로우한 사람"뿐이다. 나를 팔로우한 사람을 떼어내는 API 가 서버에 없어
    // 팔로워 탭에서는 ✕ 를 두지 않는다(눌러도 "준비 중" 만 뜨던 자리).
    val canUnfollow = isMyList && selectedTab == FollowTab.FOLLOWING

    // 조회 대상을 먼저 알린 뒤 진입 탭을 부른다. 순서가 반대면 대상이 정해지기 전에 내 목록을 불러온다.
    LaunchedEffect(targetUserId, initialTab) {
        viewModel.onIntent(FollowListIntent.SetTarget(targetUserId))
        viewModel.onIntent(FollowListIntent.SelectTab(initialTab))
    }
    // 해제 실패는 화면을 바꾸지 않고 토스트로만 알린다.
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(FollowListIntent.ConsumeError)
        }
    }

    val listState = rememberLazyListState()
    LoadMoreOnScrollEnd(listState) { viewModel.onIntent(FollowListIntent.LoadMore) }

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            FollowTopBar(onBack = { navigationHelper.navigateToBack() })
            FollowTabs(selected = selectedTab, onSelect = { viewModel.onIntent(FollowListIntent.SelectTab(it)) })
            LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (users.isEmpty()) {
                    item {
                        FollowListMessage(
                            isLoading = uiState.isLoading,
                            message =
                                uiState.loadErrorMessage
                                    ?: if (selectedTab == FollowTab.FOLLOWER) {
                                        "아직 팔로워가 없어요."
                                    } else {
                                        "아직 팔로우한 사람이 없어요."
                                    },
                            onRetry =
                                uiState.loadErrorMessage?.let {
                                    { viewModel.onIntent(FollowListIntent.Retry) }
                                },
                        )
                    }
                }
                items(users, key = { it.id }) { user ->
                    // ✕ 는 바로 해제하지 않고 확인 다이얼로그를 띄운다.
                    FollowUserRow(
                        user = user,
                        onClick = {
                            // 나를 누르면 타유저 프로필이 아니라 마이 화면으로 간다.
                            if (user.isMe) {
                                navigationHelper.navigateTo(MyRoute)
                            } else {
                                navigationHelper.navigateByRoute(UserProfilePage.route(user.handle))
                            }
                        },
                        onRemove = if (canUnfollow) ({ pendingRemoval = user }) else null,
                    )
                }
                if (uiState.isLoadingMore) {
                    item { LoadingMoreFooter() }
                }
            }
        }
        pendingRemoval?.let { user ->
            // 팔로잉 탭에서만 열리므로 해제 한 가지만 묻는다.
            DsConfirmDialog(
                title = "팔로잉을 해제할까요?",
                description = "${user.name} 님 팔로잉을 해제해요.",
                destructive = true,
                onConfirm = {
                    viewModel.onIntent(FollowListIntent.Unfollow(user.id))
                    pendingRemoval = null
                },
                onDismiss = { pendingRemoval = null },
            )
        }
    }
}

@Composable
private fun FollowTopBar(onBack: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left_24),
                contentDescription = "뒤로가기",
                tint = color.contentDefaultLevel0,
                modifier = Modifier.size(24.dp),
            )
        }
        DsText(
            text = "팔로우 목록",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
    }
}

@Composable
private fun FollowTabs(
    selected: FollowTab,
    onSelect: (FollowTab) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        FollowTabItem(
            label = "팔로워",
            active = selected == FollowTab.FOLLOWER,
            onClick = { onSelect(FollowTab.FOLLOWER) },
            modifier = Modifier.weight(1f),
        )
        FollowTabItem(
            label = "팔로잉",
            active = selected == FollowTab.FOLLOWING,
            onClick = { onSelect(FollowTab.FOLLOWING) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FollowTabItem(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = label,
            modifier = Modifier.padding(vertical = 12.dp),
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = if (active) color.contentAccent else color.contentDefaultLevel2,
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(if (active) color.contentAccent else color.borderDefaultLevel0),
        )
    }
}

@Composable
private fun FollowUserRow(
    user: FollowUserVO,
    onClick: () -> Unit,
    onRemove: (() -> Unit)?,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 서버가 주는 프로필 이미지를 그린다(URL 이 비면 기본 사람 아이콘).
        ProfileAvatar(imageUrl = user.avatarUrl, size = 44.dp)
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = user.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
            )
            // 핸들이 없으면 "@" 한 글자만 남으므로 줄째 그리지 않는다.
            if (user.handle.isNotBlank()) {
                DsText(
                    text = "@${user.handle}",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
        }
        // 남의 목록에서는 해제할 권한이 없어 버튼째 그리지 않는다.
        if (onRemove != null) {
            Box(
                modifier =
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(1.dp, color.borderDefaultLevel0, CircleShape)
                        .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.close_small_24),
                    contentDescription = "${user.name} 제거",
                    tint = color.contentDefaultLevel2,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

/** 목록이 비었을 때의 자리: 로딩 스피너 또는 안내(+재시도). */
@Composable
private fun FollowListMessage(
    isLoading: Boolean,
    message: String,
    onRetry: (() -> Unit)?,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = color.contentAccent)
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DsText(
                    text = message,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                )
                if (onRetry != null) {
                    DsText(
                        text = "다시 시도",
                        style = DesignSystemThemeImpl.typeScale.textRegularM,
                        color = color.contentAccent,
                        modifier = Modifier.clickable(onClick = onRetry),
                    )
                }
            }
        }
    }
}
