package com.chillsam.courmy.main.domain.saved

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 저장(북마크) 화면(FS-14). 로그인 상태면 저장한 코스 리스트, 비로그인이면 게스트 잠금 화면을 보여준다. */
object SavedPage : Page {
    const val PATH = "/saved"

    /** 진입 섹션. 지금은 코스 탭의 완주 목록만 지정할 수 있다(따라가기 종료 직후 이동용). */
    const val ARG_SECTION = "section"
    const val SECTION_COMPLETED_COURSES = "completedCourses"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    /** 코스 탭 · 완주 목록으로 바로 연다 — 따라가기 종료(완주 기록) 직후 확인용. */
    fun completedCourses(): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_SECTION to SECTION_COMPLETED_COURSES),
        )
}
