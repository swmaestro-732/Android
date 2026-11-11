package com.chillsam.courmy.main.presentation.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.main.entity.user.FollowRelation
import com.chillsam.courmy.main.presentation.R as MainR

/**
 * 신원 줄 아래 전체 폭으로 팔로우 상태와 요청 진행을 표시하는 버튼.
 *
 * 네 관계를 "아직 팔로우 전 / 팔로우 중" 두 모양으로 묶는다.
 * - **팔로우 전**(NONE·FOLLOWS_ME): 아직 내가 팔로우하지 않은 상태(탭=팔로우). 채워진 강조 버튼에
 *   더하기(+) 아이콘을 앞세워 '추가' 동작임을 드러낸다. 상대가 나를 팔로우 중이면 stringResource(MainR.string.main_follow_button_follow_back)로 권한다.
 * - **팔로우 중**(ONLY_I_FOLLOW·MUTUAL): 내가 팔로우 중(탭=언팔로우). 흰 배경 + 테두리에 체크
 *   아이콘을 앞세워, 이미 팔로우했음을 드러낸다. 맞팔 여부는 문구로 구분한다.
 *
 * 요청 중에는 중복 탭을 막고 버튼 안에 진행 표시를 그린다.
 */
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
                FollowRelation.NONE -> stringResource(MainR.string.main_follow_button_follow)
                FollowRelation.FOLLOWS_ME -> stringResource(MainR.string.main_follow_button_follow_back)
                FollowRelation.ONLY_I_FOLLOW -> stringResource(MainR.string.main_follow_button_following)
                FollowRelation.MUTUAL -> stringResource(MainR.string.main_follow_button_mutual_following)
            },
        modifier = modifier,
        variant = if (relation.isFollowing) DsButtonVariant.Secondary else DsButtonVariant.Primary,
        enabled = !inFlight,
        loading = inFlight,
        // 팔로우 중이면 체크, 팔로우 전이면 더하기(+)로 상태와 동작을 구분해 드러낸다.
        leadingIconRes = if (relation.isFollowing) R.drawable.ic_check_24 else R.drawable.ic_add_24,
        onClick = onClick,
    )
}
