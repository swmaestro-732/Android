package com.chillsam.courmy.main.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 편집·설정 화면 공용 상단바: 뒤로가기 셰브론 + 제목.
 *
 * @param boxed true 면 뒤로가기를 흰 라운드 박스(테두리)로 감싸고 제목을 크게(설정 FS-28 스타일),
 *   false(기본)면 플레인 셰브론 + 작은 제목(프로필 편집·관심 테마/지역 스타일).
 */
@Composable
fun BackTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    boxed: Boolean = false,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val boxStyle =
            if (boxed) {
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.bgDefaultLevel1)
                    .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(12.dp))
            } else {
                Modifier
            }
        Box(
            modifier = Modifier.size(38.dp).then(boxStyle).clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left_24),
                contentDescription = "뒤로",
                tint = color.contentDefaultLevel0,
                modifier = Modifier.size(24.dp),
            )
        }
        DsText(
            text = title,
            style =
                if (boxed) {
                    DesignSystemThemeImpl.typeScale.titleExtraL
                } else {
                    DesignSystemThemeImpl.typeScale.textStrongS
                },
            color = color.contentDefaultLevel0,
        )
    }
}
