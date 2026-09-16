package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 코스 상세 화면. 저장한 코스 목록에서 항목을 선택해 진입하며, [route] 로 courseId 를 실어 전달한다.
 * 상세 데이터는 화면에서 courseId 로 서버 조회한다.
 */
object CourseDetailPage : Page {
    const val PATH = "/course/detail"
    const val ARG_COURSE_ID = "courseId"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(courseId: String): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_COURSE_ID to courseId),
        )
}
