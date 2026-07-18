package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseDraftVO
import javax.inject.Inject

/**
 * 코스 초안 조회 UseCase.
 * 현재는 단순 pass-through(더미). 실제 임시저장/조회 API 가 붙으면 에러 처리를 추가한다.
 */
class GetCourseDraftUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        suspend operator fun invoke(): CourseDraftVO = repository.getCourseDraft()
    }
