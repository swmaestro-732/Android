package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 계획 요약 1건(`GET /api/v1/plans`). 프로필 "계획" 탭 그리드가 그린다.
 *
 * - [planId]          계획 식별자. 상세(`/service/v1/my/plans/{planId}`)·수정·삭제 요청 키다.
 * - [title]           계획 이름
 * - [plannedDateText] 계획한 날짜 표시값(예: "2026-10-10"). 날짜를 안 정했으면 빈 문자열.
 * - [placeCount]      담아 둔 장소 수
 */
@Serializable
data class PlanSummaryVO(
    val planId: Long,
    val title: String,
    val plannedDateText: String = "",
    val placeCount: Int = 0,
)

/**
 * 계획 목록 한 페이지. 커서 페이징은 코스 후기 목록과 같은 규약이다
 * (nextCursor 불투명 문자열, 없으면 끝).
 */
@Serializable
data class PlanListVO(
    val plans: List<PlanSummaryVO> = emptyList(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
) {
    companion object {
        val empty: PlanListVO = PlanListVO()
    }
}
