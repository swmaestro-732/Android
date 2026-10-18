package com.chillsam.courmy.course.domain

import javax.inject.Inject

/** 계획 삭제 UseCase. */
class DeletePlanUseCase
    @Inject
    constructor(
        private val repository: PlanRepository,
    ) {
        suspend operator fun invoke(planId: Long) {
            require(planId > 0) { "계획을 확인해 주세요." }
            repository.deletePlan(planId)
        }
    }
