package com.chillsam.courmy.main.entity.saved

/**
 * 저장함(FS-14) "저장한 코스" 카드 1건.
 * [tagLabel] 은 카드 상단 좌측 칩(예: "성수 · 데이트"), [placeLabel] 은 "장소 4곳" 형태다.
 */
data class SavedCourseVO(
    val id: String,
    val tagLabel: String,
    val title: String,
    val placeLabel: String,
    val authorHandle: String,
    val thumbnailUrl: String = "",
) {
    companion object {
        /** 개발/프리뷰용 더미 저장 코스(백엔드 미연동 시 사용). */
        val sample: List<SavedCourseVO> =
            listOf(
                SavedCourseVO(
                    id = "1",
                    tagLabel = "성수 · 데이트",
                    title = "비 오는 날 성수 감성 카페 코스",
                    placeLabel = "장소 4곳",
                    authorHandle = "routy_ai",
                ),
                SavedCourseVO(
                    id = "2",
                    tagLabel = "연남 · 브런치",
                    title = "주말 연남 느긋한 브런치 산책",
                    placeLabel = "장소 5곳",
                    authorHandle = "slow_seoul",
                ),
            )
    }
}
