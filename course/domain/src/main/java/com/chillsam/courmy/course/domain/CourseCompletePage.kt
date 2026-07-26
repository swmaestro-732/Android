package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 코스 저장 완료 화면. 여기서 마이 화면으로 이동한다. */
object CourseCompletePage : Page {
    const val PATH = "/course/complete"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
