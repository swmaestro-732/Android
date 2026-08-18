package com.chillsam.courmy.course.data.place.dto

import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.course.entity.CoursePlaceVO
import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/places/search`(외부 지도 장소 검색) 응답 DTO.
 *
 * 등록된 장소 검색([PlaceSearchEnvelope], `GET /api/v1/places`)과 달리 카카오 로컬 결과를 돌려주며,
 * 서버가 검색 시점에 내부 저장(dedup)까지 마쳐 **우리 place id** 를 함께 준다.
 * 그래서 결과를 그대로 코스 생성 요청의 `placeId` 로 쓸 수 있다.
 */
@Serializable
data class ExternalPlaceSearchEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: ExternalPlaceSearchDataDTO? = null,
)

@Serializable
data class ExternalPlaceSearchDataDTO(
    val places: List<ExternalPlaceDTO>? = null,
)

@Serializable
data class ExternalPlaceDTO(
    /** 내부 place id. 외부 검색 결과라도 서버가 저장 후 부여한 값이다. */
    val id: Long? = null,
    val name: String? = null,
    val category: String? = null,
    val roadAddress: String? = null,
    val address: String? = null,
)

fun ExternalPlaceSearchDataDTO.toVOList(): List<CoursePlaceVO> =
    places
        .orEmpty()
        // id 가 없으면 코스 생성 요청에 실을 수 없어 후보로 쓸 수 없다.
        .filter { it.id != null }
        .map { place ->
            CoursePlaceVO(
                id = place.id.toString(),
                name = place.name.orEmpty(),
                // 지도 검색 결과에는 썸네일이 없다. 카테고리 대신 주소를 보여 줘야 어떤 장소인지 구분된다.
                category = place.category.toPlaceCategoryLabel().ifBlank { place.displayAddress() },
            )
        }

/** 도로명 주소를 우선하고, 없으면 지번 주소를 쓴다. */
private fun ExternalPlaceDTO.displayAddress(): String = roadAddress?.takeIf { it.isNotBlank() } ?: address.orEmpty()
