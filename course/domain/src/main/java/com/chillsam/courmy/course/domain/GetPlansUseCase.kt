package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlanListVO
import javax.inject.Inject

/** 계획 목록 조회 UseCase. 프로필 "계획" 탭이 쓴다. 정렬·페이징은 서버 몫인 pass-through. */
class GetPlansUseCase
    @Inject
    constructor(
        private val repository: PlanRepository,
    ) {
        suspend operator fun invoke(
            cursor: String? = null,
            size: Int = PlanRepository.DEFAULT_PAGE_SIZE,
        ): PlanListVO = repository.getPlans(cursor, size)
    }
