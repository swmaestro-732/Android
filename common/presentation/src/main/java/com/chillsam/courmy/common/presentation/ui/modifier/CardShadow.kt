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
 * 카드 공용 그림자 + clip. **표면 색은 호출부가 정한다.**
 *
 * 카드와 배경(Gray200)의 명도 차가 작아, 그림자가 옅으면 카드 경계가 배경에 묻힌다.
 * 낮은 elevation 은 blur 가 거의 없어 테두리 선처럼 보이므로, elevation 을 충분히 주고
 * spot/ambient 를 함께 올려 부드럽게 퍼지면서도 분리가 확실히 보이게 한다(API 28+ 반영).
 *
 * 흰 표면 카드는 [cardShadow] 를 쓰고, 표면이 흰색이 아닌 것(이미지·지도처럼 placeholder 색을
 * 깔아 두는 자리)은 이 modifier 뒤에 자기 배경을 직접 붙인다.
 *
 * 앱의 모든 카드가 같은 깊이로 보이도록 한 곳에서 관리한다.
 */
@Composable
fun Modifier.cardElevation(shape: Shape): Modifier =
    composed {
        val color = DesignSystemThemeImpl.designSystemColor
        this
            .shadow(
                elevation = CARD_ELEVATION,
                shape = shape,
                ambientColor = color.contentDefaultLevel0.copy(alpha = AMBIENT_ALPHA),
                spotColor = color.contentDefaultLevel0.copy(alpha = SPOT_ALPHA),
            ).clip(shape)
    }

/** 흰 표면 목록 카드(홈·저장함·마이·코스 만들기 등). [cardElevation] 에 기본 카드 배경을 얹는다. */
@Composable
fun Modifier.cardShadow(shape: Shape): Modifier =
    composed {
        this
            .cardElevation(shape)
            .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
    }

private val CARD_ELEVATION = 14.dp
private const val AMBIENT_ALPHA = 0.22f
private const val SPOT_ALPHA = 0.30f
