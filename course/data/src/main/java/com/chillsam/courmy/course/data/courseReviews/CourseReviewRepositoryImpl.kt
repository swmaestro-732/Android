package com.chillsam.courmy.course.data.courseReviews

import com.chillsam.courmy.course.data.courseReviews.dto.toVO
import com.chillsam.courmy.course.domain.CourseReviewRepository
import com.chillsam.courmy.course.entity.CourseReviewListVO

class CourseReviewRepositoryImpl(
    private val dataSource: CourseReviewListDataSource,
) : CourseReviewRepository {
    override suspend fun getCourseReviews(courseId: Long): CourseReviewListVO {
        val envelope = dataSource.getCourseReviews(courseId)
        val data = requireNotNull(envelope.data) { "코스 후기 응답에 data 가 없습니다: courseId=$courseId" }
        return data.toVO()
    }
}
