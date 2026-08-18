package com.chillsam.courmy.main.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.component.BottomBarHeight

private val FabSize = 56.dp

/** FAB 그림자. 카드(cardShadow)와 같은 이유로 기본값보다 세게 준다. */
private val FabElevation = 24.dp
private const val FAB_AMBIENT_ALPHA = 0.50f
private const val FAB_SPOT_ALPHA = 0.70f

private val MenuButtonSize = 52.dp
private val ClusterSpacing = 16.dp
private val ClusterEndPadding = 20.dp

// FAB 를 하단 탭바([BottomBarHeight]) 바로 위로 띄우는 여백.
private val ClusterBottomPadding = BottomBarHeight + 16.dp

// 열림 상태에서 배경을 눌러 흐리게 처리하는 스크림 농도.
private const val SCRIM_ALPHA = 0.4f

/**
 * 홈의 "코스 만들기" FAB 클러스터.
 *
 * - 닫힘: 우하단에 `+` FAB 한 개.
 * - 열림: 배경 스크림 + `새 코스 만들기` / `임시저장 가져오기` 메뉴가 펼쳐지고, FAB 는 `x` 로 회전.
 *
 * 열림 여부([expanded])는 호출부(HomePage)가 소유하는 순수 UI 상태다.
 * 메뉴 선택([onNewCourse]·[onLoadDraft])의 실제 네비게이션은 호출부(HomePage)가 연결한다.
 * `새 코스 만들기` → course 모듈 실제 화면(CourseCreatePage, `/courseCreate`).
 */
@Composable
fun BoxScope.CreateCourseMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    onNewCourse: () -> Unit,
    onLoadDraft: () -> Unit,
) {
    // 배경 스크림: 열림 상태에서 화면 전체를 덮고, 눌러서 닫는다.
    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.matchParentSize(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = SCRIM_ALPHA))
                    .noRippleClickable(onClick = onToggle),
        )
    }

    Column(
        modifier =
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = ClusterEndPadding, bottom = ClusterBottomPadding),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(ClusterSpacing),
    ) {
        MenuEntry(
            visible = expanded,
            label = stringResource(R.string.home_menu_load_draft),
            iconRes = R.drawable.ic_draft_24,
            onClick = onLoadDraft,
        )
        MenuEntry(
            visible = expanded,
            label = stringResource(R.string.home_menu_new_course),
            iconRes = null, // `+` 아이콘은 벡터 대신 코드로 그린다.
            onClick = onNewCourse,
        )
        CreateCourseFab(expanded = expanded, onClick = onToggle)
    }
}

@Composable
private fun MenuEntry(
    visible: Boolean,
    label: String,
    @DrawableRes iconRes: Int?,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.noRippleClickable(onClick = onClick),
        ) {
            // 라벨 알약(pill)
            Box(
                modifier =
                    Modifier
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                DsText(
                    text = label,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
                )
            }
            // 원형 아이콘 버튼(흰 배경 + 초록 아이콘)
            Box(
                modifier =
                    Modifier
                        .size(MenuButtonSize)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1),
                contentAlignment = Alignment.Center,
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = DesignSystemThemeImpl.designSystemColor.contentAccent,
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    PlusIcon(
                        color = DesignSystemThemeImpl.designSystemColor.contentAccent,
                        rotation = 0f,
                        barLength = 16.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateCourseFab(
    expanded: Boolean,
    onClick: () -> Unit,
) {
    // 닫힘=`+`(0°), 열림=`x`(45°) 로 회전 애니메이션.
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "fabRotation",
    )
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(FabSize)
                // 기본 그림자 색은 너무 옅어 밝은 피드 위에서 FAB 가 배경에 붙어 보인다.
                // 크기는 그대로 두고 elevation 과 ambient/spot 을 올려 떠 있는 느낌을 만든다(API 28+ 반영).
                .shadow(
                    elevation = FabElevation,
                    shape = CircleShape,
                    ambientColor = color.contentDefaultLevel0.copy(alpha = FAB_AMBIENT_ALPHA),
                    spotColor = color.contentDefaultLevel0.copy(alpha = FAB_SPOT_ALPHA),
                ).clip(CircleShape)
                .background(color.bgAccent)
                .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        PlusIcon(
            color = color.contentOnAccent,
            rotation = rotation,
            barLength = 20.dp,
        )
    }
}

/** `+` 아이콘. [rotation] 을 45° 주면 `x` 로 보인다. 벡터 리소스 없이 두 개의 막대로 그린다. */
@Composable
private fun PlusIcon(
    color: Color,
    rotation: Float,
    barLength: Dp,
    barThickness: Dp = 2.5.dp,
) {
    Box(
        modifier = Modifier.graphicsLayer { rotationZ = rotation },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(width = barLength, height = barThickness)
                .clip(RoundedCornerShape(barThickness))
                .background(color),
        )
        Box(
            Modifier
                .size(width = barThickness, height = barLength)
                .clip(RoundedCornerShape(barThickness))
                .background(color),
        )
    }
}

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    this.composed {
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    }
