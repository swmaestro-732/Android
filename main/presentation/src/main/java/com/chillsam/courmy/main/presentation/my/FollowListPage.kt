package com.chillsam.courmy.main.presentation.my

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.user.UserProfilePage
import com.chillsam.courmy.main.entity.my.FollowUserVO

/**
 * 팔로우 목록 화면(FS-15 O). 팔로워/팔로잉 탭 전환 + 사용자 목록(제거 ✕).
 * 마이의 팔로워/팔로잉을 눌러 [initialTab] 으로 진입한다. 목록은 백엔드 연동 전 더미다.
 */
@Composable
fun FollowListPage(
    initialTab: FollowTab,
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    var selectedTab by remember { mutableStateOf(initialTab) }
    val followers = remember { FollowUserVO.sampleFollowers.toMutableStateList() }
    val following = remember { FollowUserVO.sampleFollowing.toMutableStateList() }
    val users = if (selectedTab == FollowTab.FOLLOWER) followers else following
    var pendingRemoval by remember { mutableStateOf<FollowUserVO?>(null) }

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            FollowTopBar(onBack = { navigationHelper.navigateToBack() })
            FollowTabs(selected = selectedTab, onSelect = { selectedTab = it })
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(users) { user ->
                    // ✕ 는 바로 해제하지 않고 확인 다이얼로그를 띄운다.
                    FollowUserRow(
                        user = user,
                        onClick = {
                            navigationHelper.navigateByRoute(UserProfilePage.route(user.handle))
                        },
                        onRemove = { pendingRemoval = user },
                    )
                }
            }
        }
        pendingRemoval?.let { user ->
            FollowRemoveConfirmDialog(
                user = user,
                tab = selectedTab,
                onConfirm = {
                    users.remove(user)
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
    onRemove: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.imagePlaceholder),
        )
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = user.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "@${user.handle}",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
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

/**
 * 팔로잉 해제 / 팔로워 삭제 확인 다이얼로그. 앱 공용 다이얼로그 톤(스크림 + 라운드 카드 + danger 버튼).
 * 탭에 따라 문구·확인 버튼 라벨이 달라진다.
 */
@Composable
private fun FollowRemoveConfirmDialog(
    user: FollowUserVO,
    tab: FollowTab,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val following = tab == FollowTab.FOLLOWING
    val title = if (following) "팔로잉을 해제할까요?" else "이 팔로워를 삭제할까요?"
    val desc =
        if (following) {
            "${user.name} 님 팔로잉을 해제해요."
        } else {
            "${user.name} 님을 팔로워 목록에서 삭제해요."
        }
    // 커스텀 오버레이라 시스템 Back 이 화면을 이탈시키지 않고 다이얼로그만 닫도록 가로챈다.
    BackHandler(onBack = onDismiss)
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .noRippleClickable(onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.bgDefaultLevel0)
                    .noRippleClickable {}
                    .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsText(
                text = title,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = desc,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                textAlign = TextAlign.Center,
                maxLines = Int.MAX_VALUE,
            )
            Spacer(Modifier.height(12.dp))
            DialogFilledButton(
                text = if (following) "해제" else "삭제",
                background = color.contentDanger,
                textColor = color.contentOnAccent,
                onClick = onConfirm,
            )
            DialogTextButton(text = "취소", textColor = color.contentDefaultLevel1, onClick = onDismiss)
        }
    }
}

@Composable
private fun DialogFilledButton(
    text: String,
    background: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(background)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = textColor,
        )
    }
}

@Composable
private fun DialogTextButton(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = textColor,
        )
    }
}

/** 리플 없는 클릭(스크림/카드 배경 소비용). */
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    this.composed {
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    }
