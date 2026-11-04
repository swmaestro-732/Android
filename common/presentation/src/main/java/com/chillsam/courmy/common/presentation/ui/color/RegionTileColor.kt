package com.chillsam.courmy.common.presentation.ui.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * 홈 지역별 둘러보기 **타일 지도**의 색.
 *
 * 시·도마다 제각각이면 산만해서, 같은 **권역**(수도권·강원·충청·전라·경상·제주)은 한 색으로 통합한다.
 * 광역시·특별시도 소속 권역 색을 그대로 쓰고 흰 알약으로만 구분한다. 값은 디자인 목업의 권역 대표 파스텔을
 * [LIGHTEN] 만큼 흰색으로 밝힌 톤이다(한 상수로 전체 연함을 조절). 역할 기반 시맨틱 슬롯이 아니라 범주형
 * 색이라 별도 토큰으로 두고, 화면 코드에 raw hex 를 직접 두지 않게 한다.
 */
object RegionTileColor {
    /** 권역 대표색을 흰색 쪽으로 밝히는 정도(0=원색, 1=흰색). 전체 타일 톤을 한 번에 연하게 조절한다. */
    private const val LIGHTEN = 0.4f

    private val capital = soft(Color(0xFFF6C9B4)) // 수도권: 서울·인천·경기
    private val gangwon = soft(Color(0xFFCFE3C1)) // 강원
    private val chungcheong = soft(Color(0xFFD3C6EC)) // 충청: 대전·세종·충북·충남
    private val jeolla = soft(Color(0xFFF4E3A6)) // 전라: 광주·전북·전남
    private val gyeongsang = soft(Color(0xFFF3CFCF)) // 경상: 부산·대구·울산·경북·경남
    private val jeju = soft(Color(0xFFBFE0DA)) // 제주
    private val fallback = Color(0xFFF3F3F0)

    private fun soft(base: Color): Color = lerp(base, Color.White, LIGHTEN)

    /** 시·도 2자리 코드(울릉은 "47U") → 소속 권역 색. 같은 권역은 같은 색으로 통합한다. */
    fun of(code: String): Color =
        when (code.take(2)) {
            "11", "28", "41" -> capital
            "51" -> gangwon
            "43", "44", "36", "30" -> chungcheong
            "52", "12", "29" -> jeolla
            "47", "48", "27", "31", "26" -> gyeongsang
            "50" -> jeju
            else -> fallback
        }
}
