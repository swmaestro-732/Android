package com.chillsam.courmy.course.data.courseDetail.dto

import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseReviewVO
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

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
    /** 로그인 사용자 관점 상태(저장 여부 등). 서버는 course 안에 담아 준다. */
    val viewer: CourseViewerDTO? = null,
)

@Serializable
data class CourseStatsDTO(
    val placeCount: Int? = null,
    val walkingMinutes: Int? = null,
    /** 따라간 사람 수(raw). 서버는 포맷된 문자열이 아니라 숫자를 준다. */
    val tracingCount: Int? = null,
)

/**
 * 서버가 Kotlin `val isFollowing` 으로 선언해도 Jackson 의 boolean getter 규칙상 `is` 가 떨어져
 * `following` 으로 나갈 수 있다. `ignoreUnknownKeys = true` 라 키가 어긋나면 조용히 false 가 되고
 * 팔로우 버튼이 늘 "팔로우"로 보이는 무증상 버그가 되므로, 두 표기를 모두 받는다.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AuthorDTO(
    val id: Long? = null,
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
    /** 내가 이 작성자를 팔로우 중인지. 팔로우 버튼 노출 여부를 정한다. */
    @JsonNames("following")
    val isFollowing: Boolean = false,
    /** 이 작성자가 나를 팔로우하는지. 지금은 쓰지 않지만 맞팔 표시에 필요하다. */
    @JsonNames("follower")
    val isFollower: Boolean = false,
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
 *
 * [myUserId] 는 JWT 에서 얻은 내 id 로, "내 코스" 판정에만 쓴다(서버가 isMine 을 주지 않는다).
 */
fun CourseScreenData.toVO(myUserId: Long?): CourseDetailVO {
    val course = requireNotNull(this.course) { "코스 상세 응답에 course 가 없습니다." }
    val stats = course.stats
    val summary = reviewSummary
    return CourseDetailVO(
        title = course.title.orEmpty(),
        coverImageUrl = course.coverImageUrl.orEmpty(),
        // 화면에서 칩 하나씩 그리므로 합치지 않고 목록 그대로 넘긴다.
        themes = course.themes.orEmpty(),
        authorId = course.author?.id ?: 0L,
        authorName = course.author?.nickname.orEmpty(),
        authorHandle =
            course.author
                ?.handle
                ?.let { "@$it" }
                .orEmpty(),
        authorImageUrl = course.author?.profileImageUrl.orEmpty(),
        placeCountText = "${stats?.placeCount ?: 0}곳",
        walkText = "도보 ${stats?.walkingMinutes ?: 0}분",
        followerText = "${formatTracingCount(stats?.tracingCount ?: 0)} 따라감",
        description = course.description.orEmpty(),
        places =
            course.places
                .orEmpty()
                .sortedBy { it.orderNo ?: 0 }
                .map { it.toVO() },
        rating = (summary?.averageRating ?: 0.0).toString(),
        reviewCountText = "${summary?.totalCount ?: 0}개",
        reviews = summary?.previews.orEmpty().map { it.toVO() },
        isSaved = course.viewer?.hasSaved ?: false,
        isFollowingAuthor = course.author?.isFollowing ?: false,
        isMine = myUserId != null && myUserId == course.author?.id,
    )
}

/** 따라감 수 표시(1200 → "1.2k"). 프로필 통계와 같은 규칙을 쓴다. */
private fun formatTracingCount(value: Int): String =
    if (value >= THOUSAND) {
        val truncated = (value / THOUSAND.toDouble() * 10).toInt() / 10.0
        if (truncated % 1.0 == 0.0) "${truncated.toInt()}k" else "${truncated}k"
    } else {
        value.toString()
    }

private const val THOUSAND = 1000

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
