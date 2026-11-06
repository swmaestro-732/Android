package com.chillsam.courmy.main.entity.home

/**
 * 홈의 "진행 중인 코스" 카드(FS-09). 따라가는 중(미완주) 세션이 있을 때만 서버가 내려준다 — 없으면 null 이라 카드를 숨긴다.
 *
 * "이어가기"로 그 코스 따라가기를 다시 연다([courseId]). [area]·[placeCount] 로 요약을 보여 준다.
 * 진행도(방문 수/전체 수)는 홈 응답(`tracingCourse`)에 없다 — 서버가 주면 그때 추가한다.
 */
data class HomeInProgressCourseVO(
    val courseId: Long,
    /** 따라가기 세션 id. 지금 화면은 courseId 로 이어가므로 당장 쓰지 않지만 계약에 있어 함께 싣는다. */
    val traceId: Long = 0L,
    val title: String,
    val coverImageUrl: String = "",
    /** 지역 요약(예: "을지로"). */
    val area: String = "",
    /** 코스에 담긴 장소 수. 요약에 "N곳"으로 쓴다. */
    val placeCount: Int = 0,
    /** 지금까지 방문(완료)한 장소 수. 진행도 "방문/전체" 표시에 쓴다. 서버가 안 주면 null(진행도 숨김). */
    val visitedPlaceCount: Int? = null,
    /** 코스를 더 볼 수 없는 상태(삭제·비활성·타인 비공개 전환). true 면 "비공개된 코스"로 표시하되 따라가기는 계속한다. */
    val isDeleted: Boolean = false,
    /** 시작 시각(ISO-8601 UTC). 서버가 주지 않으면 빈 문자열. */
    val startedAt: String = "",
) {
    /** 진행도를 보여 줄 수 있는지(방문 수가 있고 전체 수가 1 이상). */
    val hasProgress: Boolean
        get() = visitedPlaceCount != null && placeCount > 0

    /** 진행도 비율(0f~1f). 전체 수가 0 이면 0. 진행도 막대에 쓴다. */
    val progress: Float
        get() = if (hasProgress) ((visitedPlaceCount ?: 0).toFloat() / placeCount).coerceIn(0f, 1f) else 0f

    /** 진행도 텍스트(예: "2 / 5"). 진행도 정보가 없으면 빈 문자열. */
    val progressText: String
        get() = if (hasProgress) "${visitedPlaceCount ?: 0} / $placeCount" else ""

    /** 카드 요약 줄: 지역 · N곳 (있는 것만 이어 붙인다). */
    val summaryText: String
        get() =
            listOfNotNull(
                area.takeIf { it.isNotBlank() },
                placeCount.takeIf { it > 0 }?.let { "${it}곳" },
            ).joinToString(" · ")
}
