package com.chillsam.courmy.course.data.plans.dto

import com.chillsam.courmy.course.entity.PlanListVO
import com.chillsam.courmy.course.entity.PlanSummaryVO
import kotlinx.serialization.Serializable

/**
 * 계획 API DTO(`/api/v1/plans`). 공통 엔벨로프 `{ code, message, data }`, 전 필드 nullable.
 * 서버 스키마: PlanListResponse / PlanSummaryResponse / CreatePlanRequest / PlanIdResponse (swagger course 그룹).
 */
@Serializable
data class PlanListEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: PlanListData? = null,
)

@Serializable
data class PlanListData(
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val plans: List<PlanSummaryDTO>? = null,
)

@Serializable
data class PlanSummaryDTO(
    val planId: Long? = null,
    val title: String? = null,
    /** ISO date(yyyy-MM-dd). 날짜를 안 정한 계획은 null. */
    val plannedDate: String? = null,
    val placeCount: Int? = null,
    val updatedAt: String? = null,
)

/** 공개범위·태그·사진 없이 장소와 메모만 저장하는 비공개 계획. */
@Serializable
data class CreatePlanRequest(
    val title: String,
    val memo: String? = null,
    val plannedDate: String? = null,
    val sourceCourseId: Long? = null,
    val places: List<PlanPlaceRequest>,
)

@Serializable
data class PlanPlaceRequest(
    val placeId: Long,
    val orderNo: Int,
    val memo: String? = null,
    val walkingMinutes: Int? = null,
)

@Serializable
data class CreatePlanEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: PlanIdData? = null,
)

@Serializable
data class PlanIdData(
    val planId: Long? = null,
)

fun PlanListData.toVO(): PlanListVO =
    PlanListVO(
        plans =
            plans.orEmpty().map { plan ->
                PlanSummaryVO(
                    planId = plan.planId ?: 0L,
                    title = plan.title.orEmpty(),
                    plannedDateText = plan.plannedDate.orEmpty(),
                    placeCount = plan.placeCount ?: 0,
                )
            },
        // 커서가 없으면 hasNext 가 true 라도 끝으로 본다(코스 후기 목록과 같은 방어).
        nextCursor = nextCursor,
        hasNext = hasNext && nextCursor != null,
    )
