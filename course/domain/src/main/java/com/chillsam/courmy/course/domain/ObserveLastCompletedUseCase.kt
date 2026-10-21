package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseCompleteVO
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** 방금 저장 완료한 코스를 관찰한다(완성 화면). */
class ObserveLastCompletedUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        operator fun invoke(): StateFlow<CourseCompleteVO?> = repository.lastCompleted
    }
