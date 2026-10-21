package com.chillsam.courmy.course.data.plans

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.plans.dto.CreatePlanEnvelope
import com.chillsam.courmy.course.data.plans.dto.CreatePlanRequest
import com.chillsam.courmy.course.data.plans.dto.EditPlanRequest
import com.chillsam.courmy.course.data.plans.dto.PlanDetailEnvelope
import com.chillsam.courmy.course.data.plans.dto.PlanListEnvelope

class PlanDataSource(
    private val apiService: PlanApiService,
) : BaseRemoteDataSource() {
    suspend fun getPlanDetail(planId: Long): PlanDetailEnvelope = checkResponse(apiService.getPlanDetail(planId))

    suspend fun getPlans(
        cursor: String?,
        size: Int,
    ): PlanListEnvelope = checkResponse(apiService.getPlans(cursor = cursor, size = size))

    /** 생성 실패는 폴백 없이 예외로 올린다 — 쓰기를 가짜 성공으로 만들지 않는다. */
    suspend fun createPlan(request: CreatePlanRequest): CreatePlanEnvelope =
        checkResponse(apiService.createPlan(request))

    /** 수정·삭제도 쓰기라 폴백 없이 예외로 올린다. */
    suspend fun editPlan(
        planId: Long,
        request: EditPlanRequest,
    ): CreatePlanEnvelope = checkResponse(apiService.editPlan(planId, request))

    suspend fun deletePlan(planId: Long): CreatePlanEnvelope = checkResponse(apiService.deletePlan(planId))
}
