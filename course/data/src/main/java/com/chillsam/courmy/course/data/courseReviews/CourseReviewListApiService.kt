package com.chillsam.courmy.course.data.courseReviews

import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CourseReviewListApiService {
    /**
     * 코스 후기 전체보기(BFF). 리뷰 목록에 작성자 프로필·집계를 붙여 내려준다.
     *
     * 정렬·커서 파라미터는 **일부러 넣지 않았다** — 서버에 이 엔드포인트 구현 자체가 아직 없어
     * 계약이 확정되지 않았다. 화면의 최신순/높은 평점 토글은 받아 온 목록을 클라이언트에서 정렬한다.
     */
    @GET("service/v1/courses/{courseId}/reviews")
    suspend fun getCourseReviews(
        @Path("courseId") courseId: Long,
    ): Response<CourseReviewListEnvelope>
}
