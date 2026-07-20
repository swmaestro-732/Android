package com.chillsam.courmy.main.domain.course

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 새 코스 만들기 화면(코스 이름 입력 · 완료 · 임시저장).
 * 임시저장 목록에서 이어 작성할 때는 [route] 로 초기 제목을 실어 진입한다.
 */
object CourseCreatePage : Page {
    const val PATH = "/course/create"
    const val ARG_TITLE = "title"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    /** 임시저장 이어쓰기: 초기 제목을 채운 상태로 진입. */
    fun route(initialTitle: String): NavRoute = NavRoute(PATH, mapOf(ARG_TITLE to initialTitle))
}
