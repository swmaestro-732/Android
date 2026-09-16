package com.chillsam.courmy.course.domain

import javax.inject.Inject

/** 임시저장 목록에서 초안 1건을 지운다. */
class DeleteDraftUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        suspend operator fun invoke(courseId: Long) = repository.deleteDraft(courseId)
    }
