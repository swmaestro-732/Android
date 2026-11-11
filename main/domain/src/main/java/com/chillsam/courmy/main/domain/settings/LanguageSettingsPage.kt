package com.chillsam.courmy.main.domain.settings

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

object LanguageSettingsPage : Page {
    const val PATH = "/settings/language"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
