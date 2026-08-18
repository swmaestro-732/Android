package com.chillsam.courmy.course.domain

import javax.inject.Inject

/** 내 코스 삭제. 되돌릴 수 없으므로 화면에서 확인 다이얼로그를 거친 뒤 호출한다. */
class DeleteCourseUseCase
    @Inject
    constructor(
        private val repository: CourseManageRepository,
    ) {
        suspend operator fun invoke(courseId: Long) = repository.deleteCourse(courseId)
    }
