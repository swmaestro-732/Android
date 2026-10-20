package com.chillsam.courmy.main.presentation.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.main.entity.user.FollowRelation

/** 공통 버튼 스타일로 팔로우 상태와 요청 진행을 표시한다. */
@Composable
fun FollowButton(
    relation: FollowRelation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    inFlight: Boolean = false,
) {
    DsButton(
        text =
            when (relation) {
                FollowRelation.NONE -> "팔로우"
                FollowRelation.FOLLOWS_ME -> "맞팔로우"
                FollowRelation.ONLY_I_FOLLOW -> "팔로잉"
                FollowRelation.MUTUAL -> "서로 팔로잉"
            },
        modifier = modifier,
        variant = if (relation.isFollowing) DsButtonVariant.Secondary else DsButtonVariant.Primary,
        enabled = !inFlight,
        loading = inFlight,
        onClick = onClick,
    )
}
