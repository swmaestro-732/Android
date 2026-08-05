package com.chillsam.courmy.main.domain.saved

import javax.inject.Inject

/** 코스 저장 취소. 현재는 pass-through 이며, 실패 시 예외가 그대로 올라온다. */
class UnsaveCourseUseCase
    @Inject
    constructor(
        private val repository: SavedCourseRepository,
    ) {
        suspend operator fun invoke(courseId: Long) = repository.unsaveCourse(courseId)
    }
