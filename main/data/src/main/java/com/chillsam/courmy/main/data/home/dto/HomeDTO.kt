package com.chillsam.courmy.main.data.home.dto

import com.chillsam.courmy.common.entity.category.toCourseTagLabel
import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.common.entity.format.formatCount
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.entity.home.HomeNearbyPlaceVO
import com.chillsam.courmy.main.entity.home.HomeVO
import kotlinx.serialization.Serializable

/**
 * `GET /service/v2/home`(홈 화면 조합 BFF) 응답 DTO. 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 응답에는 `profile`(헤더는 내 프로필 조회를 그대로 씀), 코스의 `area`·`placeCount`·`walkingMinutes`,
 * 장소의 `distanceMeters` 도 오지만 화면이 쓰지 않아 매핑하지 않는다.
 */
@Serializable
data class HomeEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: HomeDTO? = null,
)

@Serializable
data class HomeDTO(
    val recommendedCourses: List<HomeRecommendedCourseDTO>? = null,
    val nearbySavedPlaces: List<HomeNearbyPlaceDTO>? = null,
)

/** v1 피드의 [CourseFeedItemDTO] 와 같은 카드 필드 구성이지만, v2 는 랭킹이 아니라 추천 결과다. */
@Serializable
data class HomeRecommendedCourseDTO(
    val id: Long? = null,
    val title: String? = null,
    val coverImageUrl: String? = null,
    /** 서버 `CourseCategory` enum 이름. 미선택 코스(draft 유래)는 null 로 온다. */
    val theme: String? = null,
    val likesCnt: Int? = null,
    val savesCnt: Int? = null,
)

@Serializable
data class HomeNearbyPlaceDTO(
    val placeId: Long? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    /** 서버 장소 카테고리 코드(`CAFE` …). */
    val category: String? = null,
    val rating: Double? = null,
    val ratingCount: Int? = null,
    val walkingMinutes: Int? = null,
    val visited: Boolean = false,
)

fun HomeDTO.toVO(): HomeVO =
    HomeVO(
        recommendedCourses =
            recommendedCourses
                .orEmpty()
                // id 가 없으면 상세로 이동할 수 없어 카드로 쓸 수 없다.
                .filter { it.id != null }
                .map { it.toVO() },
        nearbySavedPlaces =
            nearbySavedPlaces
                .orEmpty()
                .filter { it.placeId != null }
                .map { it.toVO() },
    )

internal fun HomeRecommendedCourseDTO.toVO(): HomeCourseVO =
    HomeCourseVO(
        id = requireNotNull(id).toString(),
        title = title.orEmpty(),
        coverImageUrl = coverImageUrl.orEmpty(),
        categoryLabel = theme.toCourseTagLabel(),
        saveCountText = formatCount(savesCnt ?: 0),
        likeCountText = likesCnt?.let { formatCount(it.coerceAtLeast(0)) }.orEmpty(),
    )

internal fun HomeNearbyPlaceDTO.toVO(): HomeNearbyPlaceVO =
    HomeNearbyPlaceVO(
        id = requireNotNull(placeId),
        name = name.orEmpty(),
        thumbnailUrl = imageUrl.orEmpty(),
        categoryLabel = category.toPlaceCategoryLabel(),
        // 평가가 없는 장소는 서버가 rating=0.0/ratingCount=0 으로 준다 — "0.0"을 띄우지 않게 숨긴다.
        ratingText =
            rating
                ?.takeIf { it > 0.0 && (ratingCount ?: 0) > 0 }
                ?.toString()
                .orEmpty(),
        walkingMinutes = walkingMinutes,
        visited = visited,
    )
