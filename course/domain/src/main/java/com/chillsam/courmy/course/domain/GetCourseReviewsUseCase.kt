package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseReviewListVO
import javax.inject.Inject

/**
 * 코스 후기 전체보기 조회 UseCase(FS-37).
 * [GetCourseDetailUseCase] 와 같은 pass-through 다. 실패 처리가 필요해지면 여기서 도메인 에러로 바꾼다.
 */
class GetCourseReviewsUseCase
    @Inject
    constructor(
        private val repository: CourseReviewRepository,
    ) {
        suspend operator fun invoke(courseId: Long): CourseReviewListVO = repository.getCourseReviews(courseId)
    }
