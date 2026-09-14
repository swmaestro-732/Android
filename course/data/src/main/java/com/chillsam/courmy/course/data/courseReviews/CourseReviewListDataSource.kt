package com.chillsam.courmy.course.data.courseReviews

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListEnvelope

class CourseReviewListDataSource(
    private val apiService: CourseReviewListApiService,
) : BaseRemoteDataSource() {
    /**
     * 후기 전체보기 조회.
     *
     * 이 엔드포인트는 **서버에 아직 없다**. [CourseReviewListFallback] 이 디버그 빌드에서만 실패를
     * 예시 응답으로 받아 주고, 릴리스에서는 그대로 통과시켜 예외가 화면 에러로 올라간다.
     */
    suspend fun getCourseReviews(courseId: Long): CourseReviewListEnvelope =
        CourseReviewListFallback.resolve { checkResponse(apiService.getCourseReviews(courseId)) }
}
