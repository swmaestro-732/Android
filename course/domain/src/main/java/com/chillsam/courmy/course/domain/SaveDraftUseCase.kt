package com.chillsam.courmy.course.domain

import javax.inject.Inject

/** 코스 초안을 임시저장한다. */
class SaveDraftUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        operator fun invoke(title: String) = repository.saveDraft(title)
    }
