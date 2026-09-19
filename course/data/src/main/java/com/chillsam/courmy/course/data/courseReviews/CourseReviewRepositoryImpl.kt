package com.chillsam.courmy.course.data.courseReviews

import com.chillsam.courmy.course.data.courseReviews.dto.CreateCourseReviewRequest
import com.chillsam.courmy.course.data.courseReviews.dto.toVO
import com.chillsam.courmy.course.domain.CourseReviewRepository
import com.chillsam.courmy.course.entity.CourseReviewDraftVO
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort

/**
 * [myUserId] 는 "내 리뷰"(삭제 가능) 판정에 쓴다. 서버가 리뷰마다 isMine 을 주지 않기 때문이다.
 * TokenStore 를 직접 받으면 Android 암호화 저장에 묶여 단위 테스트에서 생성할 수 없어 함수로 받는다
 * ([com.chillsam.courmy.course.data.CourseRepositoryImpl] 과 같은 방식).
 */
class CourseReviewRepositoryImpl(
    private val dataSource: CourseReviewListDataSource,
    private val myUserId: () -> Long?,
) : CourseReviewRepository {
    override suspend fun getCourseReviews(
        courseId: Long,
        sort: CourseReviewSort,
        cursor: String?,
        size: Int,
    ): CourseReviewListVO {
        val envelope =
            dataSource.getCourseReviews(
                courseId = courseId,
                sort = sort.sortKey,
                order = sort.order,
                cursor = cursor,
                size = size,
            )
        val data = requireNotNull(envelope.data) { "코스 후기 응답에 data 가 없습니다: courseId=$courseId" }
        return data.toVO(myUserId = myUserId())
    }

    override suspend fun createCourseReview(
        courseId: Long,
        draft: CourseReviewDraftVO,
    ): Long {
        val envelope =
            dataSource.createCourseReview(
                courseId = courseId,
                request =
                    CreateCourseReviewRequest(
                        rating = draft.rating,
                        // 빈 본문은 보내지 않는다. 서버 content 는 nullable 이고, 공백만 저장하면
                        // "본문 있는 리뷰" 로 보여 카드에 빈 줄이 생긴다.
                        content = draft.content?.trim()?.takeIf { it.isNotEmpty() },
                        photoUrls = draft.photoUrls,
                        tagCodes = draft.tagCodes,
                    ),
            )
        return requireNotNull(envelope.data?.reviewId) { "후기 작성 응답에 reviewId 가 없습니다: courseId=$courseId" }
    }

    override suspend fun deleteCourseReview(
        courseId: Long,
        reviewId: Long,
    ) {
        dataSource.deleteCourseReview(courseId = courseId, reviewId = reviewId)
    }
}
