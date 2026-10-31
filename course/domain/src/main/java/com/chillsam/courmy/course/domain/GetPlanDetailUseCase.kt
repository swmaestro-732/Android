package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlanDetailVO
import javax.inject.Inject

/**
 * 계획 상세 조회 UseCase.
 *
 * 계획 상세 BFF(`GET /service/v1/my/plans/{planId}`)가 장소의 이름·카테고리·주소·좌표까지
 * 함께 내려주므로, 과거처럼 장소별 상세를 병렬 조회(N+1)해 합치지 않는다.
 */
class GetPlanDetailUseCase
    @Inject
    constructor(
        private val repository: PlanRepository,
    ) {
        suspend operator fun invoke(planId: Long): PlanDetailVO {
            require(planId > 0) { "계획을 확인해 주세요." }
            return repository.getPlanDetail(planId)
        }
    }
