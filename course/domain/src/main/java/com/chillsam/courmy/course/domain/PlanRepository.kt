package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlanInputVO
import com.chillsam.courmy.course.entity.PlanListVO

/**
 * 계획(플랜) Repository.
 *
 * 계획은 "가 보고 싶은 곳을 미리 묶어 둔 것"으로 코스의 전 단계다(생성 시 sourceCourseId 로
 * 코스에서 파생될 수 있다). 서버 분류도 course 그룹이라 course 모듈이 소유한다.
 */
interface PlanRepository {
    suspend fun getPlanDetail(planId: Long): com.chillsam.courmy.course.entity.PlanDetailVO

    /** 계획 목록 한 페이지(`GET /api/v1/plans`). [cursor] null 이면 첫 페이지. */
    suspend fun getPlans(
        cursor: String? = null,
        size: Int = DEFAULT_PAGE_SIZE,
    ): PlanListVO

    /** 장소 1~10곳을 담은 비공개 계획을 만들고 planId를 돌려준다(`POST /api/v1/plans`). */
    suspend fun createPlan(plan: PlanInputVO): Long

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20
    }
}
