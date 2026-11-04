package com.chillsam.courmy.common.presentation.ui.color

import androidx.compose.ui.graphics.Color
import com.chillsam.courmy.common.presentation.ui.token.CourmyPaletteColors

/**
 * 지역별 둘러보기 6권역 블롭 색.
 *
 * 역할 기반 시맨틱 슬롯([DesignSystemSemanticColors])과 달리 "권역 구분"이라는 범주형 색이라
 * 별도 토큰으로 노출한다. 값은 테마 일러스트(`img_theme_*`)에 쓰인 파스텔에서 뽑아 팔레트에 등록했다.
 * raw hex 를 화면 코드에 직접 두지 않도록 반드시 이 enum 을 거친다.
 */
enum class RegionColor(
    private val palette: CourmyPaletteColors,
) {
    CAPITAL(CourmyPaletteColors.RegionCapital),
    GANGWON(CourmyPaletteColors.RegionGangwon),
    CHUNGCHEONG(CourmyPaletteColors.RegionChungcheong),
    JEOLLA(CourmyPaletteColors.RegionJeolla),
    GYEONGSANG(CourmyPaletteColors.RegionGyeongsang),
    JEJU(CourmyPaletteColors.RegionJeju),
    ;

    val color: Color get() = palette.colorValue
}
