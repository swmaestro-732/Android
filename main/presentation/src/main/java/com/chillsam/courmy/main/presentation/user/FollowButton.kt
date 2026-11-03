package com.chillsam.courmy.main.presentation.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.user.FollowRelation

/**
 * 팔로우 상태를 원형 아이콘 버튼으로 표시한다.
 *
 * 팔로우 전은 강조색 배경에 사람+ 아이콘, 팔로잉 중은 옅은 배경에 체크 아이콘으로
 * 눌린 상태를 구분한다(텍스트 버튼의 Primary/Secondary 구분을 아이콘으로 옮겼다).
 * 요청 중에는 중복 탭을 막고 진행 표시를 그린다.
 */
@Composable
fun FollowButton(
    relation: FollowRelation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    inFlight: Boolean = false,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val following = relation.isFollowing
    val background = if (following) color.bgDefaultLevel1 else color.bgAccent
    val tint = if (following) color.contentDefaultLevel1 else color.contentOnAccent
    val iconRes = if (following) R.drawable.ic_check_24 else R.drawable.ic_person_add_24
    val description =
        when (relation) {
            FollowRelation.NONE -> "팔로우"
            FollowRelation.FOLLOWS_ME -> "맞팔로우"
            FollowRelation.ONLY_I_FOLLOW -> "팔로잉"
            FollowRelation.MUTUAL -> "서로 팔로잉"
        }
    Box(
        modifier =
            modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(background)
                .clickable(enabled = !inFlight, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (inFlight) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = tint,
            )
        } else {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = description,
                tint = tint,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
