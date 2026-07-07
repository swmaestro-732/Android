package com.chillsam.courmy.favorite.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

object FavoritePage : Page {
    const val PATH = "/favorite"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
