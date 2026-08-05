package com.chillsam.courmy.course.data.placeDetail.dto

import com.chillsam.courmy.course.entity.PlaceDetailVO
import kotlinx.serialization.Serializable
import java.util.Locale

/**
 * `GET /service/v1/places/{placeId}`(장소 상세 화면 조합) 응답 DTO.
 * 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 응답의 `nearbyCourses`("이 근처 코스")는 현재 시트가 렌더하지 않아 매핑하지 않는다.
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
    val imageUrls: List<String>? = null,
    val address: String? = null,
    val openStatus: String? = null,
    val openingHoursText: String? = null,
    val reviewSummary: PlaceReviewSummaryDTO? = null,
)

@Serializable
data class PlaceReviewSummaryDTO(
    val averageRating: Double? = null,
    val totalCount: Int? = null,
)

/**
 * 장소 상세 변환.
 *
 * TODO-API-SPEC: 시트의 저장 수·지역·특징 태그는 응답에 필드가 없어 비워 둔다(빈 값이면 렌더하지 않는다).
 * 도보 안내([PlaceDetailVO.walkText])는 장소 자체가 아니라 "이 코스에서 다음 장소까지" 값이라
 * 장소 상세가 아닌 코스 상세에서 받아 [walkText] 로 넘긴다. [wiki-needed]
 */
fun PlaceDetailScreenDTO.toVO(walkText: String): PlaceDetailVO {
    val place = this.place
    val summary = place?.reviewSummary
    return PlaceDetailVO(
        name = place?.name.orEmpty(),
        category = place?.categories.orEmpty().joinToString(" · "),
        heroImageUrl = place?.imageUrls?.firstOrNull().orEmpty(),
        rating = summary?.averageRating?.let { String.format(Locale.US, "%.1f", it) }.orEmpty(),
        reviewCountText = summary?.totalCount?.toString().orEmpty(),
        savedCountText = "",
        isOpen = place?.openStatus == OPEN_STATUS,
        openStatusText = place?.openStatus.toOpenStatusText(),
        walkText = walkText,
        areaText = "",
        imageUrls = place?.imageUrls.orEmpty(),
        tags = emptyList(),
        address = place?.address.orEmpty(),
        hoursText = place?.openingHoursText.orEmpty(),
    )
}

private const val OPEN_STATUS = "OPEN"

/** 서버 영업 상태 enum 을 화면 문구로. 모르는 값은 빈 문자열(칩을 렌더하지 않는다). */
private fun String?.toOpenStatusText(): String =
    when (this) {
        OPEN_STATUS -> "영업중"
        "CLOSED" -> "영업 종료"
        "BREAK" -> "브레이크 타임"
        "DAY_OFF" -> "휴무일"
        else -> ""
    }
