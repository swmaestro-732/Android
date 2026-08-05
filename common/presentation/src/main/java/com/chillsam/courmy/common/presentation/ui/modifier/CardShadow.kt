package com.chillsam.courmy.common.presentation.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 목록 카드 공용 그림자 + 배경.
 *
 * 카드(White)와 배경(Gray200)의 명도 차가 작아, 그림자가 옅으면 카드 경계가 배경에 묻힌다.
 * 낮은 elevation 은 blur 가 거의 없어 테두리 선처럼 보이므로, elevation 을 충분히 주고
 * spot/ambient 를 함께 올려 부드럽게 퍼지면서도 분리가 확실히 보이게 한다(API 28+ 반영).
 *
 * 홈·저장함·마이의 카드가 같은 깊이로 보이도록 한 곳에서 관리한다.
 */
@Composable
fun Modifier.cardShadow(shape: Shape): Modifier =
    composed {
        val color = DesignSystemThemeImpl.designSystemColor
        this
            .shadow(
                elevation = CARD_ELEVATION,
                shape = shape,
                ambientColor = color.contentDefaultLevel0.copy(alpha = AMBIENT_ALPHA),
                spotColor = color.contentDefaultLevel0.copy(alpha = SPOT_ALPHA),
            ).clip(shape)
            .background(color.bgDefaultLevel1)
    }

private val CARD_ELEVATION = 14.dp
private const val AMBIENT_ALPHA = 0.22f
private const val SPOT_ALPHA = 0.30f
