package com.chillsam.courmy.course.data.plans

import com.chillsam.courmy.course.data.plans.dto.CreatePlanRequest
import com.chillsam.courmy.course.data.plans.dto.EditPlanRequest
import com.chillsam.courmy.course.data.plans.dto.PlanPlaceRequest
import com.chillsam.courmy.course.data.plans.dto.toVO
import com.chillsam.courmy.course.domain.PlanRepository
import com.chillsam.courmy.course.entity.PlanInputVO
import com.chillsam.courmy.course.entity.PlanListVO

class PlanRepositoryImpl(
    private val dataSource: PlanDataSource,
) : PlanRepository {
    override suspend fun getPlanDetail(planId: Long): com.chillsam.courmy.course.entity.PlanDetailVO =
        requireNotNull(dataSource.getPlanDetail(planId).data) { "계획 상세 응답에 data가 없습니다." }.toVO()

    override suspend fun getPlans(
        cursor: String?,
        size: Int,
    ): PlanListVO {
        val envelope = dataSource.getPlans(cursor = cursor, size = size)
        val data = requireNotNull(envelope.data) { "계획 목록 응답에 data 가 없습니다" }
        return data.toVO()
    }

    override suspend fun createPlan(plan: PlanInputVO): Long {
        val envelope =
            dataSource.createPlan(
                CreatePlanRequest(
                    title = plan.title.trim(),
                    // 빈 메모는 보내지 않는다 — 공백만 저장하면 "메모 있는 계획"으로 보인다.
                    memo = plan.memo?.trim()?.takeIf { it.isNotEmpty() },
                    plannedDate = plan.plannedDate,
                    sourceCourseId = plan.sourceCourseId,
                    places =
                        plan.places.mapIndexed { index, place ->
                            PlanPlaceRequest(
                                placeId = place.placeId,
                                orderNo = index,
                                memo = place.memo?.trim()?.takeIf { it.isNotEmpty() },
                                walkingMinutes = place.walkingMinutes.takeUnless { index == plan.places.lastIndex },
                            )
                        },
                ),
            )
        return requireNotNull(envelope.data?.planId) { "계획 생성 응답에 planId 가 없습니다" }
    }

    override suspend fun editPlan(
        planId: Long,
        plan: PlanInputVO,
    ) {
        dataSource.editPlan(
            planId = planId,
            request =
                EditPlanRequest(
                    title = plan.title.trim(),
                    // 생성과 같은 규칙: 공백만 있는 메모는 보내지 않는다.
                    memo = plan.memo?.trim()?.takeIf { it.isNotEmpty() },
                    plannedDate = plan.plannedDate,
                    places =
                        plan.places.mapIndexed { index, place ->
                            PlanPlaceRequest(
                                placeId = place.placeId,
                                orderNo = index,
                                memo = place.memo?.trim()?.takeIf { it.isNotEmpty() },
                                walkingMinutes = place.walkingMinutes.takeUnless { index == plan.places.lastIndex },
                            )
                        },
                ),
        )
    }

    override suspend fun deletePlan(planId: Long) {
        dataSource.deletePlan(planId)
    }
}
