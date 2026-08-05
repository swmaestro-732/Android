package com.chillsam.courmy.course.data.courseDetail.dto

import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseReviewVO
import kotlinx.serialization.Serializable

/**
 * 코스 상세 화면 조합 응답(BFF) DTO. `GET /service/v1/courses/{courseId}`.
 * 공통 엔벨로프 `{ code, message, data }` 에서 [data] 안에 course + reviewSummary 가 들어온다.
 * DTO 는 전 필드 nullable, VO 변환([toVO])은 이 data 레이어에서만 수행한다.
 */
@Serializable
data class CourseDetailEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseScreenData? = null,
)

@Serializable
data class CourseScreenData(
    val course: CourseScreenDTO? = null,
    val reviewSummary: ReviewSummaryDTO? = null,
    /** 로그인 사용자 관점 상태(저장 여부 등). 비로그인이면 서버가 false 로 채운다. */
    val viewer: CourseViewerDTO? = null,
)

@Serializable
data class CourseViewerDTO(
    val hasSaved: Boolean = false,
)

@Serializable
data class CourseScreenDTO(
    val title: String? = null,
    val coverImageUrl: String? = null,
    val themes: List<String>? = null,
    val description: String? = null,
    val stats: CourseStatsDTO? = null,
    val author: AuthorDTO? = null,
    val places: List<CoursePlaceDTO>? = null,
)

@Serializable
data class CourseStatsDTO(
    val placeCount: Int? = null,
    val walkingMinutes: Int? = null,
    val tracingCountLabel: String? = null,
)

@Serializable
data class AuthorDTO(
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
)

@Serializable
data class CoursePlaceDTO(
    /** place 도메인 식별자. 장소 상세(`GET /service/v1/places/{placeId}`) 조회 키다(코스 내 식별자 `id` 와 다르다). */
    val placeId: Long? = null,
    val orderNo: Int? = null,
    val name: String? = null,
    val caption: String? = null,
    val walkingMinutesToNext: Int? = null,
    val categories: List<String>? = null,
    val location: CoursePlaceLocationDTO? = null,
    val images: List<CoursePlaceImageDTO>? = null,
)

@Serializable
data class CoursePlaceLocationDTO(
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class CoursePlaceImageDTO(
    val imageUrl: String? = null,
    val orderNo: Int? = null,
)

@Serializable
data class ReviewSummaryDTO(
    val averageRating: Double? = null,
    val totalCount: Int? = null,
    val previews: List<ReviewPreviewDTO>? = null,
)

@Serializable
data class ReviewPreviewDTO(
    val author: ReviewAuthorDTO? = null,
    val rating: Int? = null,
    val content: String? = null,
    val relativeTime: String? = null,
    val photoUrls: List<String>? = null,
)

@Serializable
data class ReviewAuthorDTO(
    val nickname: String? = null,
    val profileImageUrl: String? = null,
)

/**
 * BFF 응답 → 화면 표시용 VO. raw 값을 화면 표시 문자열("4곳", "도보 20분" 등)로 포맷팅한다.
 */
fun CourseScreenData.toVO(): CourseDetailVO {
    val course = requireNotNull(this.course) { "코스 상세 응답에 course 가 없습니다." }
    val stats = course.stats
    val summary = reviewSummary
    return CourseDetailVO(
        title = course.title.orEmpty(),
        coverImageUrl = course.coverImageUrl.orEmpty(),
        category = course.themes.orEmpty().joinToString(SEPARATOR),
        authorName = course.author?.nickname.orEmpty(),
        authorHandle =
            course.author
                ?.handle
                ?.let { "@$it" }
                .orEmpty(),
        authorImageUrl = course.author?.profileImageUrl.orEmpty(),
        placeCountText = "${stats?.placeCount ?: 0}곳",
        walkText = "도보 ${stats?.walkingMinutes ?: 0}분",
        followerText = "${stats?.tracingCountLabel ?: "0"} 따라감",
        description = course.description.orEmpty(),
        places =
            course.places
                .orEmpty()
                .sortedBy { it.orderNo ?: 0 }
                .map { it.toVO() },
        rating = (summary?.averageRating ?: 0.0).toString(),
        reviewCountText = "${summary?.totalCount ?: 0}개",
        reviews = summary?.previews.orEmpty().map { it.toVO() },
        isSaved = viewer?.hasSaved ?: false,
    )
}

private fun CoursePlaceDTO.toVO(): CourseDetailPlaceVO {
    val urls =
        images
            .orEmpty()
            .sortedBy { it.orderNo ?: 0 }
            .mapNotNull { it.imageUrl }
    return CourseDetailPlaceVO(
        placeId = placeId ?: 0L,
        order = (orderNo ?: 0) + 1,
        name = name.orEmpty(),
        category = categories.orEmpty().joinToString(SEPARATOR),
        photoCountText = "1/${urls.size.coerceAtLeast(1)}",
        tip = caption.orEmpty(),
        imageUrls = urls,
        walkToNextText = walkingMinutesToNext?.let { "도보 ${it}분" },
        latitude = location?.latitude,
        longitude = location?.longitude,
    )
}

private fun ReviewPreviewDTO.toVO(): CourseReviewVO =
    CourseReviewVO(
        author = author?.nickname ?: "익명",
        authorImageUrl = author?.profileImageUrl.orEmpty(),
        rating = rating ?: 0,
        dateText = relativeTime.orEmpty(),
        body = content.orEmpty(),
        photoUrls = photoUrls.orEmpty(),
    )

private const val SEPARATOR = " · "
