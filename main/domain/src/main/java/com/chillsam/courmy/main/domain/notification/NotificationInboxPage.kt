package com.chillsam.courmy.main.domain.notification

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

object NotificationInboxPage : Page {
    const val PATH = "/my/notifications"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
