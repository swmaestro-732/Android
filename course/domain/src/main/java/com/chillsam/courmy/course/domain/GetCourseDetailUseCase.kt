package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseDetailVO
import javax.inject.Inject

/** 코스 상세를 조회한다. */
class GetCourseDetailUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        suspend operator fun invoke(courseId: String): CourseDetailVO = repository.getCourseDetail(courseId)
    }
