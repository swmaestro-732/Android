package com.chillsam.courmy.course.data.plans

import com.chillsam.courmy.course.data.plans.dto.CreatePlanRequest
import com.chillsam.courmy.course.data.plans.dto.toVO
import com.chillsam.courmy.course.domain.PlanRepository
import com.chillsam.courmy.course.entity.PlanListVO

class PlanRepositoryImpl(
    private val dataSource: PlanDataSource,
) : PlanRepository {
    override suspend fun getPlans(
        cursor: String?,
        size: Int,
    ): PlanListVO {
        val envelope = dataSource.getPlans(cursor = cursor, size = size)
        val data = requireNotNull(envelope.data) { "계획 목록 응답에 data 가 없습니다" }
        return data.toVO()
    }

    override suspend fun createPlan(
        title: String,
        memo: String?,
    ): Long {
        val envelope =
            dataSource.createPlan(
                CreatePlanRequest(
                    title = title.trim(),
                    // 빈 메모는 보내지 않는다 — 공백만 저장하면 "메모 있는 계획"으로 보인다.
                    memo = memo?.trim()?.takeIf { it.isNotEmpty() },
                ),
            )
        return requireNotNull(envelope.data?.planId) { "계획 생성 응답에 planId 가 없습니다" }
    }
}
