package com.chillsam.courmy.course.data.placeReviews

import com.chillsam.courmy.course.data.courseReviews.dto.toVO
import com.chillsam.courmy.course.domain.PlaceReviewRepository
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
}
