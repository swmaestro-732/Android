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
    /** 지역 표시 문자열 원본(예: "성수"). 검색 지역 필터 옵션 수집에 쓴다. */
    val area: String = "",
    val coverImageUrl: String = "",
    val spotCount: Int = 0,
    val durationHours: Int = 0,
    val authorName: String = "",
    /** 따라감 수 표시 문자열(예: "1.2k명"). */
    val followCountText: String = "",
    /** 서버 집계 표시값. 미제공이면 빈 문자열로 숨긴다. */
    val likeCountText: String = "",
    val saveCountText: String = "",
    /** 저장 토글 시 집계에 ±1 반영할 원시 값. 미제공이면 null(집계 미조정). */
    val saveCount: Int? = null,
)
