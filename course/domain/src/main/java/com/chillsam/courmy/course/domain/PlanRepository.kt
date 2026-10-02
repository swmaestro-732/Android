package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlanListVO

/**
 * 계획(플랜) Repository.
 *
 * 계획은 "가 보고 싶은 곳을 미리 묶어 둔 것"으로 코스의 전 단계다(생성 시 sourceCourseId 로
 * 코스에서 파생될 수 있다). 서버 분류도 course 그룹이라 course 모듈이 소유한다.
 */
interface PlanRepository {
    /** 계획 목록 한 페이지(`GET /api/v1/plans`). [cursor] null 이면 첫 페이지. */
    suspend fun getPlans(
        cursor: String? = null,
        size: Int = DEFAULT_PAGE_SIZE,
    ): PlanListVO

    /**
     * 계획을 만들고 생성된 planId 를 돌려준다(`POST /api/v1/plans`).
     *
     * 장소 없이 제목만으로도 만들 수 있다(서버 places minItems 0) — 홈 FAB "계획하기"가
     * 이 경로다. 장소는 이후 계획 상세에서 붙인다.
     */
    suspend fun createPlan(
        title: String,
        memo: String? = null,
    ): Long

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20
    }
}
