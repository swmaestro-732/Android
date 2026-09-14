package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseReviewListVO

/**
 * 코스 리뷰 조회 Repository.
 *
 * 코스 자체(작성·보관·상세)와 리뷰는 다른 관심사이고 엔드포인트도 따로라서 [CourseRepository] 에
 * 얹지 않고 나눠 뒀다. 앞으로 리뷰 작성·삭제가 붙을 자리이기도 하다.
 */
interface CourseReviewRepository {
    /** 코스 후기 전체보기(FS-37)를 서버(`GET /service/v1/courses/{id}/reviews`)에서 조회한다. */
    suspend fun getCourseReviews(courseId: Long): CourseReviewListVO
}
