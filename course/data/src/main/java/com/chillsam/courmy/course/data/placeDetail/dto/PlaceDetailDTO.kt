package com.chillsam.courmy.course.data.placeDetail.dto

import com.chillsam.courmy.course.entity.PlaceDetailVO
import kotlinx.serialization.Serializable

/**
 * `GET /service/v1/places/{placeId}`(장소 상세 화면 조합) 응답 DTO.
 * 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 현재 서버가 채워 주는 장소 필드는 이름·카테고리·주소·좌표뿐이다. 평점·리뷰 수·영업 상태·영업시간·
 * 사진은 응답에 없어 시트에서 걷어냈으므로 DTO 에서도 받지 않는다(`ignoreUnknownKeys = true` 라
 * 서버가 나중에 다시 내려줘도 파싱은 깨지지 않는다). 응답의 `nearbyCourses`("이 근처 코스")도
 * 시트가 렌더하지 않아 매핑하지 않는다. [wiki-needed]
 */
@Serializable
data class PlaceDetailEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: PlaceDetailScreenDTO? = null,
)

@Serializable
data class PlaceDetailScreenDTO(
    val place: PlaceScreenDTO? = null,
)

@Serializable
data class PlaceScreenDTO(
    val id: Long? = null,
    val name: String? = null,
    val categories: List<String>? = null,
    val address: String? = null,
    val location: PlaceLocationDTO? = null,
)

/** 좌표. 코스 상세의 `CoursePlaceLocationDTO` 와 같은 표기를 쓴다. */
@Serializable
data class PlaceLocationDTO(
    val latitude: Double? = null,
    val longitude: Double? = null,
)

/** 장소 상세 변환. 좌표가 한쪽만 오면 지도를 그릴 수 없으므로 둘 다 있을 때만 채운다. */
fun PlaceDetailScreenDTO.toVO(): PlaceDetailVO {
    val place = this.place
    val location = place?.location
    val hasCoordinates = location?.latitude != null && location.longitude != null
    return PlaceDetailVO(
        name = place?.name.orEmpty(),
        category = place?.categories.orEmpty().joinToString(" · "),
        address = place?.address.orEmpty(),
        latitude = location?.latitude.takeIf { hasCoordinates },
        longitude = location?.longitude.takeIf { hasCoordinates },
    )
}
