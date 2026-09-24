package com.chillsam.courmy.main.entity.search

/**
 * 검색 결과의 코스 카드 1건(FS-18).
 *
 * 홈 피드의 [com.chillsam.courmy.main.entity.home.HomeCourseVO] 와 따로 두는 건 카드가 보여주는
 * 값이 달라서다 — 여기는 스팟 수·소요 시간·작성자·따라감 수를 한 줄로 요약해 붙인다.
 *
 * 따라감 수는 서버가 주는 숫자를 그대로 두지 않고 표시 문자열로 받는다("1.2k명"). 자리에 따라
 * 축약 규칙이 달라질 수 있어 화면이 매번 다시 계산하지 않도록 data 레이어에서 정해 넘긴다.
 */
data class SearchCourseVO(
    val id: String,
    val title: String,
    /** 커버 좌상단 배지(예: "성수 · 데이트"). */
    val categoryLabel: String = "",
    val coverImageUrl: String = "",
    val spotCount: Int = 0,
    val durationHours: Int = 0,
    val authorName: String = "",
    /** 따라감 수 표시 문자열(예: "1.2k명"). */
    val followCountText: String = "",
)
