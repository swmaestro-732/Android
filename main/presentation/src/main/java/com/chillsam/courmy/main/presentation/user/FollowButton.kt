package com.chillsam.courmy.main.presentation.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.user.FollowRelation

private val FollowButtonHeight = 52.dp
private val FollowButtonCornerRadius = 16.dp
private const val IN_FLIGHT_ALPHA = 0.6f

/**
 * 타유저 프로필의 팔로우 버튼(FS-15). [FollowRelation] 4가지에 라벨·스타일이 1:1 대응한다.
 *
 * - 아직 팔로우 안 함 → 채운 강조 버튼(bgAccent #30623C · contentOnAccent)
 * - 이미 팔로우 중 → 외곽선 버튼(bgDefaultLevel0 #F5F5F3 · borderAccent · contentAccent)
 *
 * 라벨은 아직 팔로우하지 않은 상태만 행동 유도형("…하기")이고, 이미 팔로우 중이면 상태 서술형("…중")이다.
 */
@Composable
fun FollowButton(
    relation: FollowRelation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    inFlight: Boolean = false,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isOutlined = relation.isFollowing

    val background =
        when {
            isOutlined -> color.bgDefaultLevel0
            isPressed -> color.bgAccentPressed
            else -> color.bgAccent
        }
    val contentColor = if (isOutlined) color.contentAccent else color.contentOnAccent

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(FollowButtonHeight)
                .alpha(if (inFlight) IN_FLIGHT_ALPHA else 1f)
                .clip(RoundedCornerShape(FollowButtonCornerRadius))
                .background(background)
                .then(
                    if (isOutlined) {
                        Modifier.border(
                            width = 1.dp,
                            color = color.borderAccent,
                            shape = RoundedCornerShape(FollowButtonCornerRadius),
                        )
                    } else {
                        Modifier
                    },
                ).clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !inFlight,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = relation.followButtonLabel(),
            style = DesignSystemThemeImpl.typeScale.textRegularM,
            color = contentColor,
        )
    }
}

/** Figma FS-15 의 팔로우 버튼 라벨 4종. */
private fun FollowRelation.followButtonLabel(): String =
    when (this) {
        FollowRelation.NONE -> "팔로우하기"
        FollowRelation.FOLLOWS_ME -> "나도 팔로우하기"
        FollowRelation.ONLY_I_FOLLOW -> "나만 팔로우 중"
        FollowRelation.MUTUAL -> "서로 팔로우 중"
    }
