package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import javax.inject.Inject

/** 계획 삭제 UseCase. */
class DeletePlanUseCase
    @Inject
    constructor(
        private val repository: PlanRepository,
    ) {
        suspend operator fun invoke(planId: Long) {
            requireLocalized(planId > 0) { LocalizedValidationException(StringResource.VALIDATION_PLAN_TARGET) }
            repository.deletePlan(planId)
        }
    }
