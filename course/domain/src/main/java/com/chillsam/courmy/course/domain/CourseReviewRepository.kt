package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseReviewDraftVO
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort

/**
 * 코스 리뷰 Repository.
 *
 * 코스 자체(작성·보관·상세)와 리뷰는 다른 관심사이고 엔드포인트도 따로라서 [CourseRepository] 에
 * 얹지 않고 나눠 뒀다. 조회는 BFF(`service/v1`), 작성·삭제는 도메인 API(`api/v1`) 라 경로도 갈린다.
 */
interface CourseReviewRepository {
    /**
     * 코스 후기 한 페이지를 조회한다(`GET /service/v1/courses/{id}/reviews`).
     *
     * [cursor] 가 null 이면 첫 페이지다. 다음 페이지는 직전 결과의 `nextCursor` 를 그대로 넘긴다.
     * 정렬은 서버가 수행하므로 [sort] 만 바꿔도 목록 순서가 달라진다.
     */
    suspend fun getCourseReviews(
        courseId: Long,
        sort: CourseReviewSort,
        cursor: String? = null,
        size: Int = DEFAULT_PAGE_SIZE,
    ): CourseReviewListVO

    /**
     * 후기를 작성하고 생성된 리뷰 id 를 돌려준다(`POST /api/v1/courses/{id}/reviews`).
     *
     * 코스당 1인 1건이라 이미 쓴 코스면 서버가 409 로 거절한다.
     * [CourseReviewDraftVO.photoUrls] 는 업로드가 끝난 공개 URL 이어야 한다.
     */
    suspend fun createCourseReview(
        courseId: Long,
        draft: CourseReviewDraftVO,
    ): Long

    /** 내 후기를 삭제한다(`DELETE /api/v1/courses/{id}/reviews/{reviewId}`). */
    suspend fun deleteCourseReview(
        courseId: Long,
        reviewId: Long,
    )

    companion object {
        /** 한 번에 받아 올 후기 수. 서버는 1~50 만 받는다. */
        const val DEFAULT_PAGE_SIZE: Int = 10
    }
}
