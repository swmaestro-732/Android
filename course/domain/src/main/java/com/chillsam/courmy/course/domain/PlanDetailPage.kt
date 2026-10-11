package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

object PlanDetailPage : Page {
    const val PATH = "/plan/detail"
    const val ARG_PLAN_ID = "planId"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(planId: String): NavRoute = NavRoute(PATH, args = mapOf(ARG_PLAN_ID to planId))
}
