package com.chillsam.courmy.course.domain

import javax.inject.Inject

/** 계획 생성 UseCase. 홈 FAB "계획하기"가 제목만으로 빈 계획을 만든다. */
class CreatePlanUseCase
    @Inject
    constructor(
        private val repository: PlanRepository,
    ) {
        suspend operator fun invoke(
            title: String,
            memo: String? = null,
        ): Long = repository.createPlan(title = title, memo = memo)
    }
