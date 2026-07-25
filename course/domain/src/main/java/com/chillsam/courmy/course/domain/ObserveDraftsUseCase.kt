package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.SavedCourseVO
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** 임시저장 코스 목록을 관찰한다. */
class ObserveDraftsUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        operator fun invoke(): StateFlow<List<SavedCourseVO>> = repository.drafts
    }
