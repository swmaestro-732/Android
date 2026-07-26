package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseDraftVO
import javax.inject.Inject

/** 현재 작성 중인 코스 초안을 임시저장한다(같은 초안은 덮어써 중복을 만들지 않는다). */
class SaveDraftUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        operator fun invoke(draft: CourseDraftVO) = repository.saveDraft(draft)
    }
