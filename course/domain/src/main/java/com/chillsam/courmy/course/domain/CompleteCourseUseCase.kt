package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseCompleteVO
import javax.inject.Inject

/** 코스 저장을 완료한다(완성 화면 데이터 보관 + 저장 목록 추가). */
class CompleteCourseUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        operator fun invoke(course: CourseCompleteVO?) = repository.completeCourse(course)
    }
