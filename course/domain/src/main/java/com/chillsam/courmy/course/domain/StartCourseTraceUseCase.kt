package com.chillsam.courmy.course.domain

import javax.inject.Inject

/** 따라가기 시작 기록 UseCase. 발급된 traceId 를 돌려준다. */
class StartCourseTraceUseCase
    @Inject
    constructor(
        private val repository: TraceRepository,
    ) {
        suspend operator fun invoke(courseId: Long): Long {
            require(courseId > 0) { "코스를 확인해 주세요." }
            return repository.startTrace(courseId)
        }
    }
