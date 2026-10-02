package com.chillsam.courmy.course.data.plans

import com.chillsam.courmy.course.data.plans.dto.CreatePlanEnvelope
import com.chillsam.courmy.course.data.plans.dto.CreatePlanRequest
import com.chillsam.courmy.course.data.plans.dto.PlanListEnvelope
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PlanApiService {
    /** 계획 목록. [cursor] 는 직전 응답의 nextCursor 를 그대로 돌려주는 불투명 문자열(첫 페이지 null). */
    @GET("api/v1/plans")
    suspend fun getPlans(
        @Query("cursor") cursor: String?,
        @Query("size") size: Int,
    ): Response<PlanListEnvelope>

    /** 계획 생성. 장소 없이 제목만으로도 만들 수 있다(places minItems 0). */
    @POST("api/v1/plans")
    suspend fun createPlan(
        @Body request: CreatePlanRequest,
    ): Response<CreatePlanEnvelope>
}
