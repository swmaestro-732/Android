package com.chillsam.courmy.main.domain.settings

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

object NotificationSettingsPage : Page {
    const val PATH = "/settings/notifications"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
