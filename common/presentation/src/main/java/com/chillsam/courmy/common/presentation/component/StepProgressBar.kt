package com.chillsam.courmy.common.presentation.component

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
 * 여러 단계로 나뉜 플로우 상단의 진행 바. [total]칸 중 앞에서부터 [step]칸을 강조색으로 채운다.
 *
 * 회원가입(프로필 1 · 관심 테마 2 · 관심 지역 3 · 완료 4)과 코스 만들기가 함께 쓴다.
 * 두 플로우가 같은 모양이어야 해서 common 에 둔다(course 는 main 에 의존할 수 없다).
 */
@Composable
fun StepProgressBar(
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
