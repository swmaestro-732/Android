package com.chillsam.courmy.search.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

object SearchPage : Page {
    const val PATH = "/search"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
