package com.chillsam.courmy.intro.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

object IntroPage : Page {
    const val PATH = ""

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
