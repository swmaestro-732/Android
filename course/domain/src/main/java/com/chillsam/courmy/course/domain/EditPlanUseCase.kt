package com.chillsam.courmy.course.domain

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
            require(planId > 0) { "계획을 확인해 주세요." }
            require(plan.title.isNotBlank()) { "계획 이름을 입력해 주세요." }
            repository.editPlan(planId, plan)
        }
    }
