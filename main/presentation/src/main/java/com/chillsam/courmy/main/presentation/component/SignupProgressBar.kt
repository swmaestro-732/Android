package com.chillsam.courmy.main.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 회원가입 플로우 상단의 단계 진행 바. [total]칸 중 앞에서부터 [step]칸을 강조색으로 채운다.
 * (프로필 1 · 관심 테마 2 · 관심 지역 3 · 완료 4)
 */
@Composable
fun SignupProgressBar(
    step: Int,
    modifier: Modifier = Modifier,
    total: Int = 4,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(total) { index ->
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (index < step) color.bgAccent else color.borderDefaultLevel1),
            )
        }
    }
}
