package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 코스 편집 화면. 코스 상세의 "코스 편집하기"로 진입하며 [route] 로 courseId 를 실어 전달한다.
 * 편집할 값은 화면에서 courseId 로 코스 상세를 다시 조회해 채운다.
 */
object CourseEditPage : Page {
    const val PATH = "/course/edit"
    const val ARG_COURSE_ID = "courseId"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(courseId: String): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_COURSE_ID to courseId),
        )
}
