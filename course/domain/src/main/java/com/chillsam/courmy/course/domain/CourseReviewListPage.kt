package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 코스 후기 전체보기 화면(FS-37). 코스 상세의 "전체보기" 로 진입하며 [route] 로 courseId 를 싣는다.
 * 목록·집계는 화면에서 courseId 로 서버 조회한다.
 */
object CourseReviewListPage : Page {
    const val PATH = "/course/reviews"
    const val ARG_COURSE_ID = "courseId"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(courseId: String): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_COURSE_ID to courseId),
        )
}
