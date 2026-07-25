package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.SavedCourseVO
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** 저장 완료한 코스 목록을 관찰한다(마이 화면). */
class ObserveSavedCoursesUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        operator fun invoke(): StateFlow<List<SavedCourseVO>> = repository.savedCourses
    }
