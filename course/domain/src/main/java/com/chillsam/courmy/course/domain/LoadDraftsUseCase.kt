package com.chillsam.courmy.course.domain

import javax.inject.Inject

/** 기기에 저장해 둔 임시저장 초안을 목록으로 올린다. 목록 화면 진입 시 한 번 부른다. */
class LoadDraftsUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        suspend operator fun invoke() = repository.loadDrafts()
    }
