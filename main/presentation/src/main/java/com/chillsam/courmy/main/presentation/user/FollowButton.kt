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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.user.FollowRelation

/** 팔로우 버튼의 상태별 모양(아이콘·배경·틴트·접근성 라벨). */
private data class FollowButtonVisual(
    val iconRes: Int,
    val background: Color,
    val tint: Color,
    val description: String,
)

/**
 * 팔로우 상태를 원형 아이콘 버튼으로 표시한다. 네 관계를 세 모양으로 묶는다.
 *
 * - **팔로우 전**(NONE·FOLLOWS_ME): 아직 내가 팔로우하지 않은 상태(탭=팔로우)라 한 모양으로 묶는다.
 *   강조색 배경에 사람+ 아이콘.
 * - **팔로잉**(ONLY_I_FOLLOW): 나만 팔로우 중. 옅은 배경에 체크.
 * - **맞팔**(MUTUAL): 서로 팔로우 중. 옅은 강조 틴트 배경에 사람 둘 아이콘으로 세운다.
 *
 * 요청 중에는 중복 탭을 막고 원형 진행 표시를 그린다. 접근성 라벨은 관계별로 구분한다.
 */
@Composable
fun FollowButton(
    relation: FollowRelation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    inFlight: Boolean = false,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val visual =
        when (relation) {
            FollowRelation.NONE -> {
                FollowButtonVisual(R.drawable.ic_person_add_24, color.bgAccent, color.contentOnAccent, "팔로우")
            }

            FollowRelation.FOLLOWS_ME -> {
                FollowButtonVisual(R.drawable.ic_person_add_24, color.bgAccent, color.contentOnAccent, "나도 팔로우하기")
            }

            FollowRelation.ONLY_I_FOLLOW -> {
                FollowButtonVisual(R.drawable.ic_check_24, color.bgDefaultLevel1, color.contentDefaultLevel2, "팔로잉")
            }

            FollowRelation.MUTUAL -> {
                FollowButtonVisual(R.drawable.ic_group_24, color.bgAccentSubtle, color.contentAccent, "서로 팔로잉")
            }
        }
    Box(
        modifier =
            modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(visual.background)
                .clickable(enabled = !inFlight, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (inFlight) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = visual.tint,
            )
        } else {
            Icon(
                painter = painterResource(visual.iconRes),
                contentDescription = visual.description,
                tint = visual.tint,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
