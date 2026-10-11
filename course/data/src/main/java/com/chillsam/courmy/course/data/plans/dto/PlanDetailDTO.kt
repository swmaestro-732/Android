package com.chillsam.courmy.course.data.plans.dto

import com.chillsam.courmy.course.entity.PlanDetailPlaceVO
import com.chillsam.courmy.course.entity.PlanDetailVO
import kotlinx.serialization.Serializable

@Serializable
data class PlanDetailEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: PlanDetailDTO? = null,
)

@Serializable
data class PlanDetailDTO(
    val planId: Long? = null,
    val title: String? = null,
    val memo: String? = null,
    val plannedDate: String? = null,
    val sourceCourseId: Long? = null,
    val places: List<PlanDetailPlaceDTO>? = null,
)

@Serializable
data class PlanDetailPlaceDTO(
    val placeId: Long? = null,
    val orderNo: Int? = null,
    val memo: String? = null,
    val walkingMinutesToNext: Int? = null,
)

fun PlanDetailDTO.toVO(): PlanDetailVO =
    PlanDetailVO(
        planId = requireNotNull(planId),
        title = title.orEmpty().ifBlank { "나의 계획" },
        memo = memo.orEmpty(),
        plannedDate = plannedDate.orEmpty(),
        sourceCourseId = sourceCourseId?.takeIf { it > 0 },
        places =
            places.orEmpty().sortedBy { it.orderNo ?: 0 }.map { place ->
                PlanDetailPlaceVO(
                    placeId = requireNotNull(place.placeId),
                    memo = place.memo.orEmpty(),
                    walkingMinutesToNext = place.walkingMinutesToNext,
                )
            },
    )
