package com.chillsam.courmy.course.data.plans

import com.chillsam.courmy.course.data.plans.dto.CreatePlanEnvelope
import com.chillsam.courmy.course.data.plans.dto.CreatePlanRequest
import com.chillsam.courmy.course.data.plans.dto.EditPlanRequest
import com.chillsam.courmy.course.data.plans.dto.PlanDetailEnvelope
import com.chillsam.courmy.course.data.plans.dto.PlanListEnvelope
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlanApiService {
    /**
     * 계획 상세 화면 조합(BFF). raw `api/v1/plans/{planId}` 와 달리 장소의 이름·카테고리·주소·좌표를
     * 함께 내려줘 장소별 상세 N+1 조회가 필요 없다. 스펙의 `userId` 쿼리는 springdoc 오표기(JWT 정본).
     */
    @GET("service/v1/my/plans/{planId}")
    suspend fun getPlanDetail(
        @Path("planId") planId: Long,
    ): Response<PlanDetailEnvelope>

    /** 계획 목록. [cursor] 는 직전 응답의 nextCursor 를 그대로 돌려주는 불투명 문자열(첫 페이지 null). */
    @GET("api/v1/plans")
    suspend fun getPlans(
        @Query("cursor") cursor: String?,
        @Query("size") size: Int,
    ): Response<PlanListEnvelope>

    /** 계획 생성. 방문 순서대로 장소 1~10곳을 담는다. */
    @POST("api/v1/plans")
    suspend fun createPlan(
        @Body request: CreatePlanRequest,
    ): Response<CreatePlanEnvelope>

    /** 계획 수정. places 는 전체 교체 계약이다. 응답은 생성과 같은 { planId } 봉투. */
    @PATCH("api/v1/plans/{planId}")
    suspend fun editPlan(
        @Path("planId") planId: Long,
        @Body request: EditPlanRequest,
    ): Response<CreatePlanEnvelope>

    /** 계획 삭제. 응답 data 는 null 이라 봉투만 확인한다. */
    @DELETE("api/v1/plans/{planId}")
    suspend fun deletePlan(
        @Path("planId") planId: Long,
    ): Response<CreatePlanEnvelope>
}
