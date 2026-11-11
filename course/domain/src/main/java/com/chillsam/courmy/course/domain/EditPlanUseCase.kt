package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.course.entity.PlanInputVO
import javax.inject.Inject

/** 계획 수정 UseCase. 서버가 title 을 필수로 요구하므로 빈 제목은 요청 전에 거른다. */
class EditPlanUseCase
    @Inject
    constructor(
        private val repository: PlanRepository,
    ) {
        suspend operator fun invoke(
            planId: Long,
            plan: PlanInputVO,
        ) {
            requireLocalized(planId > 0) { LocalizedValidationException(StringResource.VALIDATION_PLAN_TARGET) }
            requireLocalized(
                plan.title.isNotBlank(),
            ) { LocalizedValidationException(StringResource.VALIDATION_PLAN_REQUIRED) }
            repository.editPlan(planId, plan)
        }
    }
