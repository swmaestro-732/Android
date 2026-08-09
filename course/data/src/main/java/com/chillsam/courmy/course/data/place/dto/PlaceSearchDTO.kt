package com.chillsam.courmy.course.data.place.dto

import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.common.entity.paging.CursorPageVO
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
    val location: PlaceLocationDTO? = null,
    val hasSaved: Boolean = false,
)

/** 장소 좌표. 코스 생성에서 장소 사이 도보 시간을 구하는 데 쓴다. */
@Serializable
data class PlaceLocationDTO(
    val latitude: Double? = null,
    val longitude: Double? = null,
)

/**
 * 검색 결과 → 코스에 담는 장소 카드.
 * [CoursePlaceVO.id] 는 서버 place id 문자열이며, 코스 생성 시 다시 Long 으로 바꿔 `placeId` 로 보낸다.
 */
fun PlaceSearchDataDTO.toPageVO(): CursorPageVO<CoursePlaceVO> =
    CursorPageVO(
        items = places.orEmpty().map { it.toVO() },
        nextCursor = nextCursor?.takeIf { it.isNotBlank() },
        // 커서가 없으면 더 받을 수 없으므로, 서버가 hasNext=true 로 줘도 끝으로 본다.
        hasNext = hasNext && !nextCursor.isNullOrBlank(),
    )

fun PlaceDTO.toVO(): CoursePlaceVO =
    CoursePlaceVO(
        id = (id ?: 0L).toString(),
        name = name.orEmpty(),
        category = categories.toPlaceCategoryLabel(),
        thumbnailUrl = imageUrl.orEmpty(),
        // 좌표는 한쪽만 있으면 쓸 수 없어 둘 다 있을 때만 채운다.
        latitude = location?.latitude?.takeIf { location.longitude != null },
        longitude = location?.longitude?.takeIf { location.latitude != null },
    )
