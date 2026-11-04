package com.chillsam.courmy.common.presentation.ui.color

import androidx.compose.ui.graphics.Color

/**
 * 홈 지역별 둘러보기 **타일 지도**의 시·도별 정확한 파스텔.
 *
 * 디자인 목업(그리드 타일 시안)에서 지정된 값 그대로다. 역할 기반 시맨틱 슬롯이 아니라
 * 지역 구분용 범주형 색이라 별도 토큰으로 둔다. 화면 코드에 raw hex 를 직접 두지 않도록 이 맵을 거친다.
 * 키는 시·도 법정동 2자리(울릉은 경북 소속 섬이라 "47U"로 구분).
 */
object RegionTileColor {
    private val byCode =
        mapOf(
            "41" to Color(0xFFF6C9B4), // 경기
            "11" to Color(0xFFEEAD8E), // 서울
            "28" to Color(0xFFFADACB), // 인천
            "51" to Color(0xFFCFE3C1), // 강원
            "43" to Color(0xFFCABDEA), // 충북
            "44" to Color(0xFFDCD3F0), // 충남
            "36" to Color(0xFFECE7F8), // 세종
            "30" to Color(0xFFB7A7E2), // 대전
            "47" to Color(0xFFF5C6C6), // 경북
            "47U" to Color(0xFFF5C6C6), // 울릉(경북)
            "27" to Color(0xFFEBA4A4), // 대구
            "31" to Color(0xFFF0B4B4), // 울산
            "26" to Color(0xFFE89A9A), // 부산
            "52" to Color(0xFFF7E2A6), // 전북
            "12" to Color(0xFFFAEDC6), // 전남(전남·광주 통합)
            "29" to Color(0xFFEFCB6E), // 광주
            "48" to Color(0xFFF9DADA), // 경남
            "50" to Color(0xFFBFE0DA), // 제주
        )

    /** 시·도 코드(2자리, 울릉은 "47U")에 해당하는 타일 색. 없으면 옅은 회색으로 떨어뜨린다. */
    fun of(code: String): Color = byCode[code] ?: Color(0xFFEDEDEA)
}
