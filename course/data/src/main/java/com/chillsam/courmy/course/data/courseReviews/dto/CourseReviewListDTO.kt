package com.chillsam.courmy.course.data.courseReviews.dto

import com.chillsam.courmy.common.entity.time.toRelativeTimeText
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

/**
 * 서버 `CourseReviewListResponse`.
 *
 * 사진만 모아 둔 필드는 **없다**. 상단 사진 띠는 [reviews] 의 사진을 펼쳐서 만들고,
 * 총 장수만 [photoCount] 로 받는다(전체 기준이라 지금 페이지 사진 수보다 클 수 있다).
 */
@Serializable
data class CourseReviewListData(
    val averageRating: Double? = null,
    val totalCount: Int? = null,
    val photoCount: Int? = null,
    val ratingDistribution: List<RatingCountDTO>? = null,
    /** 내가 이 코스를 완주했는지. 서버 판정이 아직 고정값이라 작성 가능 여부로 쓰지 않는다. */
    val hasCompletedCourse: Boolean = false,
    /** 다음 페이지 커서(불투명 문자열). 마지막 페이지면 null. */
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val reviews: List<CourseReviewItemDTO>? = null,
)

@Serializable
data class RatingCountDTO(
    val rating: Int? = null,
    val count: Int? = null,
)

/**
 * 리뷰 1건.
 *
 * [createdAt] 은 ISO-8601 UTC 문자열이다. 서버는 "3분 전" 같은 상대 시각 문구를 주지 않으므로
 * 앱이 변환한다([toRelativeTimeText]).
 */
@Serializable
data class CourseReviewItemDTO(
    val id: Long? = null,
    val author: ReviewItemAuthorDTO? = null,
    val rating: Int? = null,
    /** 별점만 남긴 리뷰는 null 이다. */
    val content: String? = null,
    val createdAt: String? = null,
    val photoUrls: List<String>? = null,
    val tags: List<ReviewItemTagDTO>? = null,
)

@Serializable
data class ReviewItemAuthorDTO(
    val id: Long? = null,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
)

/** [code] 는 서버 `CourseReviewTag` enum 이름의 소문자다(예: "packed"). 작성 요청에 다시 싣는 값이다. */
@Serializable
data class ReviewItemTagDTO(
    val code: String? = null,
    val label: String? = null,
    val icon: String? = null,
)

/**
 * BFF 응답 → 화면 표시용 VO. 비율·"+N" 같은 표시값 계산을 여기서 끝낸다.
 *
 * 별점 분포 비율의 분모는 `totalCount` 가 아니라 **분포 합계**다. 서버가 1·2점 구간을 빼고 내려주거나
 * totalCount 와 분포가 어긋나도 막대가 넘치지 않는다.
 *
 * [myUserId] 는 JWT 에서 얻은 내 id 로 "내 리뷰"(삭제 가능) 판정에만 쓴다 — 서버가 주지 않는다.
 * [nowMillis] 는 상대 시각 계산 기준이며 테스트가 시각을 고정하려고 넘긴다.
 */
fun CourseReviewListData.toVO(
    myUserId: Long?,
    nowMillis: Long = System.currentTimeMillis(),
): CourseReviewListVO {
    val buckets = ratingDistribution.orEmpty()
    val bucketTotal = buckets.sumOf { it.count ?: 0 }
    val items = reviews.orEmpty().map { it.toVO(myUserId, nowMillis) }
    // 서버가 사진 목록을 따로 주지 않아 리뷰에 붙은 사진을 순서대로 펼쳐 띠를 만든다.
    val photos = items.flatMap { it.photoUrls }
    val totalPhotos = photoCount ?: photos.size
    // 마지막 칸이 "+N" 으로 덮이므로, 접힌 개수는 전체에서 앞 3칸을 뺀 값이다.
    val hiddenPhotos = totalPhotos - (PHOTO_STRIP_SLOTS - 1)
    return CourseReviewListVO(
        averageRatingText = (averageRating ?: 0.0).toString(),
        averageStars = (averageRating ?: 0.0).roundToInt(),
        totalCountText = "${totalCount ?: 0}개",
        totalCount = totalCount,
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
        photoCountText = totalPhotos.takeIf { it > 0 }?.toString().orEmpty(),
        photoUrls = photos.take(PHOTO_STRIP_SLOTS),
        morePhotoCountText = if (photos.size >= PHOTO_STRIP_SLOTS && hiddenPhotos > 0) "+$hiddenPhotos" else "",
        reviews = items,
        // 커서가 없으면 hasNext 가 true 라도 끝으로 본다. 같은 페이지를 무한히 다시 받지 않기 위해서다.
        nextCursor = nextCursor,
        hasNext = hasNext && nextCursor != null,
        hasCompletedCourse = hasCompletedCourse,
    )
}

private fun CourseReviewItemDTO.toVO(
    myUserId: Long?,
    nowMillis: Long,
): CourseReviewVO =
    CourseReviewVO(
        id = id ?: 0L,
        authorId = author?.id ?: 0L,
        author = author?.nickname ?: "익명",
        authorImageUrl = author?.profileImageUrl.orEmpty(),
        rating = rating ?: 0,
        dateText = createdAt.toRelativeTimeText(nowMillis),
        body = content.orEmpty(),
        photoUrls = photoUrls.orEmpty(),
        tags =
            tags
                .orEmpty()
                // 문구가 없는 태그는 칩에 그릴 게 없다. 이모지만 떠 있는 칩을 만들지 않는다.
                .filter { !it.label.isNullOrBlank() }
                .map {
                    CourseReviewTagVO(
                        code = it.code.orEmpty(),
                        label = it.label.orEmpty(),
                        icon = it.icon.orEmpty(),
                    )
                },
        // 비로그인(myUserId == null)이나 작성자 id 가 없는 응답에서 모두 "내 리뷰" 가 되지 않도록 둘 다 확인한다.
        isMine = myUserId != null && author?.id != null && author.id == myUserId,
    )
