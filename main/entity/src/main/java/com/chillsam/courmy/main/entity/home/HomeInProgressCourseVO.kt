package com.chillsam.courmy.main.entity.home

/**
 * 홈의 "진행 중이던 코스" 카드(FS-09). 따라가는 중(미완주) 세션이 있을 때만 서버가 내려준다 — 없으면 null 이라 카드를 숨긴다.
 *
 * [visitedCount]/[totalCount] 로 진행도(예: 2/4)를 보여 주고, "이어가기"로 그 코스 따라가기를 다시 연다.
 */
data class HomeInProgressCourseVO(
    val courseId: Long,
    val title: String,
    val coverImageUrl: String = "",
    val visitedCount: Int = 0,
    val totalCount: Int = 0,
    /** 다음에 갈 장소 이름. 서버가 주지 않으면 빈 문자열이고 화면은 진행도만 쓴다. */
    val nextPlaceName: String = "",
) {
    /** 진행도 표기(예: "2/4"). 총 장소 수가 0이면 빈 문자열. */
    val progressText: String get() = if (totalCount > 0) "$visitedCount/$totalCount" else ""
}
