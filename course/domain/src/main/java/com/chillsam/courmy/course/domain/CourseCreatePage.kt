package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 코스 만들기 화면(FS-34 CourseCreateActivity) 네비게이션 식별자.
 * args 없는 단일 세그먼트 라우트.
 */
object CourseCreatePage : Page {
    const val PATH = "/courseCreate"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
