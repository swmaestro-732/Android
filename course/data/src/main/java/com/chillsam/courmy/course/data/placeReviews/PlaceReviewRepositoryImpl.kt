package com.chillsam.courmy.course.data.placeReviews

import com.chillsam.courmy.course.data.courseReviews.dto.CreateCourseReviewRequest
import com.chillsam.courmy.course.data.courseReviews.dto.toVO
import com.chillsam.courmy.course.domain.PlaceReviewRepository
import com.chillsam.courmy.course.entity.CourseReviewDraftVO
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort

/**
 * [myUserId] 는 "내 리뷰" 판정에 쓴다 — 서버가 리뷰마다 isMine 을 주지 않는다.
 * TokenStore 를 직접 받지 않고 함수로 받는 이유는
 * [com.chillsam.courmy.course.data.courseReviews.CourseReviewRepositoryImpl] 과 같다(단위 테스트).
 */
class PlaceReviewRepositoryImpl(
    private val dataSource: PlaceReviewListDataSource,
    private val myUserId: () -> Long?,
) : PlaceReviewRepository {
    override suspend fun getPlaceReviews(
        placeId: Long,
        sort: CourseReviewSort,
        cursor: String?,
        size: Int,
    ): CourseReviewListVO {
        val envelope =
            dataSource.getPlaceReviews(
                placeId = placeId,
                sort = sort.sortKey,
                order = sort.order,
                cursor = cursor,
                size = size,
            )
        val data = requireNotNull(envelope.data) { "장소 후기 응답에 data 가 없습니다: placeId=$placeId" }
        return data.toVO(myUserId = myUserId())
    }

    override suspend fun createPlaceReview(
        placeId: Long,
        draft: CourseReviewDraftVO,
    ): Long {
        val envelope =
            dataSource.createPlaceReview(
                placeId = placeId,
                request =
                    CreateCourseReviewRequest(
                        rating = draft.rating,
                        // 빈 본문은 보내지 않는다. 공백만 저장하면 "본문 있는 리뷰" 로 보여
                        // 카드에 빈 줄이 생긴다(코스 후기와 같은 규칙).
                        content = draft.content?.trim()?.takeIf { it.isNotEmpty() },
                        photoUrls = draft.photoUrls,
                        tagCodes = draft.tagCodes,
                    ),
            )
        return requireNotNull(envelope.data?.reviewId) { "후기 작성 응답에 reviewId 가 없습니다: placeId=$placeId" }
    }

    override suspend fun deletePlaceReview(
        placeId: Long,
        reviewId: Long,
    ) {
        dataSource.deletePlaceReview(placeId = placeId, reviewId = reviewId)
    }
}
