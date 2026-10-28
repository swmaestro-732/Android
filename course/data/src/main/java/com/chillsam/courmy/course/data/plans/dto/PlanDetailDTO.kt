package com.chillsam.courmy.course.data.plans.dto

import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.course.entity.PlanDetailPlaceVO
import com.chillsam.courmy.course.entity.PlanDetailVO
import kotlinx.serialization.Serializable

/**
 * `GET /service/v1/my/plans/{planId}`(계획 상세 화면 조합 BFF) 응답 DTO.
 *
 * 장소에 이름·카테고리·주소·좌표·이미지가 함께 와서 장소별 상세 조회가 필요 없다.
 * `placeCount`·`walkingMinutes`(합계)·`imageUrl` 은 화면이 쓰지 않아 매핑하지 않는다.
 */
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
    val name: String? = null,
    /** 서버 장소 카테고리 코드 목록(`CAFE` …). */
    val categories: List<String>? = null,
    val address: String? = null,
    val location: PlanPlaceLocationDTO? = null,
)

@Serializable
data class PlanPlaceLocationDTO(
    val latitude: Double? = null,
    val longitude: Double? = null,
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
                    name = place.name.orEmpty(),
                    categoryLabel = place.categories.toPlaceCategoryLabel(),
                    address = place.address.orEmpty(),
                    latitude = place.location?.latitude,
                    longitude = place.location?.longitude,
                )
            },
    )
