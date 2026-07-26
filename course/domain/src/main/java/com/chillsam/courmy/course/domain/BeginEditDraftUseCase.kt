package com.chillsam.courmy.course.domain

import javax.inject.Inject

/**
 * 임시저장 목록에서 "이어서 편집"을 눌렀을 때, 다음 코스 만들기 진입이 해당 초안을
 * 불러오도록 편집 대상을 id 로 지정한다.
 */
class BeginEditDraftUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        operator fun invoke(draftId: String) = repository.beginEditDraft(draftId)
    }
