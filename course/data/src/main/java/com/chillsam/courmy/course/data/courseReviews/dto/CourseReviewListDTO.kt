package com.chillsam.courmy.course.data.courseReviews.dto

import com.chillsam.courmy.course.entity.CourseRatingBucketVO
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewTagVO
import com.chillsam.courmy.course.entity.CourseReviewVO
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

/** 사진 띠에 늘어놓는 칸 수. 넘치는 개수는 마지막 칸에 "+N" 으로 접는다(Figma FS-37 은 4칸). */
private const val PHOTO_STRIP_SLOTS = 4

/** 별점 분포에 그리는 구간 수(5점부터 내림차순). Figma 는 상위 3줄만 보여준다. */
private const val DISTRIBUTION_ROWS = 3

/**
 * 코스 후기 전체보기 응답(BFF) DTO. `GET /service/v1/courses/{courseId}/reviews`.
 * 공통 엔벨로프 `{ code, message, data }` 형태이며 DTO 는 전 필드 nullable 이다.
 */
@Serializable
data class CourseReviewListEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseReviewListData? = null,
)

@Serializable
data class CourseReviewListData(
    val averageRating: Double? = null,
    val totalCount: Int? = null,
    val photoCount: Int? = null,
    val photoUrls: List<String>? = null,
    val ratingDistribution: List<RatingCountDTO>? = null,
    val reviews: List<CourseReviewItemDTO>? = null,
)

@Serializable
data class RatingCountDTO(
    val rating: Int? = null,
    val count: Int? = null,
)

@Serializable
data class CourseReviewItemDTO(
    val author: ReviewItemAuthorDTO? = null,
    val rating: Int? = null,
    val content: String? = null,
    val relativeTime: String? = null,
    val photoUrls: List<String>? = null,
    val tags: List<ReviewItemTagDTO>? = null,
)

@Serializable
data class ReviewItemAuthorDTO(
    val nickname: String? = null,
    val profileImageUrl: String? = null,
)

@Serializable
data class ReviewItemTagDTO(
    val label: String? = null,
    val icon: String? = null,
)

/**
 * BFF 응답 → 화면 표시용 VO. 비율·"+N" 같은 표시값 계산을 여기서 끝낸다.
 *
 * 별점 분포 비율의 분모는 `totalCount` 가 아니라 **분포 합계**다. 서버가 1·2점 구간을 빼고 내려주거나
 * totalCount 와 분포가 어긋나도 막대가 넘치지 않는다.
 */
fun CourseReviewListData.toVO(): CourseReviewListVO {
    val buckets = ratingDistribution.orEmpty()
    val bucketTotal = buckets.sumOf { it.count ?: 0 }
    val photos = photoUrls.orEmpty()
    val shownPhotos = photos.take(PHOTO_STRIP_SLOTS)
    // 마지막 칸이 "+N" 으로 덮이므로, 접힌 개수는 전체에서 앞 3칸을 뺀 값이다.
    val hiddenPhotos = (photoCount ?: photos.size) - (PHOTO_STRIP_SLOTS - 1)
    return CourseReviewListVO(
        averageRatingText = (averageRating ?: 0.0).toString(),
        averageStars = (averageRating ?: 0.0).roundToInt(),
        totalCountText = "${totalCount ?: 0}개",
        distribution =
            buckets
                .sortedByDescending { it.rating ?: 0 }
                .take(DISTRIBUTION_ROWS)
                .map { bucket ->
                    CourseRatingBucketVO(
                        rating = bucket.rating ?: 0,
                        ratio = if (bucketTotal > 0) (bucket.count ?: 0).toFloat() / bucketTotal else 0f,
                    )
                },
        photoCountText = (photoCount ?: photos.size).takeIf { it > 0 }?.toString().orEmpty(),
        photoUrls = shownPhotos,
        morePhotoCountText = if (photos.size >= PHOTO_STRIP_SLOTS && hiddenPhotos > 0) "+$hiddenPhotos" else "",
        reviews = reviews.orEmpty().map { it.toVO() },
    )
}

private fun CourseReviewItemDTO.toVO(): CourseReviewVO =
    CourseReviewVO(
        author = author?.nickname ?: "익명",
        authorImageUrl = author?.profileImageUrl.orEmpty(),
        rating = rating ?: 0,
        dateText = relativeTime.orEmpty(),
        body = content.orEmpty(),
        photoUrls = photoUrls.orEmpty(),
        tags =
            tags
                .orEmpty()
                // 문구가 없는 태그는 칩에 그릴 게 없다. 이모지만 떠 있는 칩을 만들지 않는다.
                .filter { !it.label.isNullOrBlank() }
                .map { CourseReviewTagVO(label = it.label.orEmpty(), icon = it.icon.orEmpty()) },
    )
