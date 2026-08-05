package com.chillsam.courmy.course.data.place.dto

import com.chillsam.courmy.course.entity.CoursePlaceVO
import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/places` 응답 DTO(장소 검색).
 *
 * 같은 이름의 `GET /api/v1/places/search` 는 카카오 원본 검색이라 **id 가 없어** 코스에 담을 수 없다.
 * 코스 생성 요청의 `places[].placeId` 로 쓸 수 있는 건 이쪽(서버 DB 등록분)뿐이다.
 */
@Serializable
data class PlaceSearchEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: PlaceSearchDataDTO? = null,
)

@Serializable
data class PlaceSearchDataDTO(
    val totalCount: Int? = null,
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val places: List<PlaceDTO>? = null,
)

@Serializable
data class PlaceDTO(
    val id: Long? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val categories: List<String>? = null,
    val averageRating: Double? = null,
    val reviewCount: Int? = null,
    val walkingMinutes: Int? = null,
    val hasSaved: Boolean = false,
)

/**
 * 검색 결과 → 코스에 담는 장소 카드.
 * [CoursePlaceVO.id] 는 서버 place id 문자열이며, 코스 생성 시 다시 Long 으로 바꿔 `placeId` 로 보낸다.
 */
fun PlaceDTO.toVO(): CoursePlaceVO =
    CoursePlaceVO(
        id = (id ?: 0L).toString(),
        name = name.orEmpty(),
        category = categories.orEmpty().joinToString(" · "),
        thumbnailUrl = imageUrl.orEmpty(),
    )
