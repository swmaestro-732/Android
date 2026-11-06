package com.chillsam.courmy.common.presentation.helper

import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.message.MessageEffect
import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.NavSignal
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.navigation.Page
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class NavigationHelperImpl : NavigationHelper {
    private val _navigationFlow = Channel<NavSignal>(capacity = Channel.BUFFERED)
    override val navigationFlow: Flow<NavSignal> = _navigationFlow.receiveAsFlow()

    override fun navigateTo(
        page: Page,
        source: NavigationSource?,
    ) {
        navigateByRoute(page.toRoute(), source)
    }

    override fun navigateByRoute(
        route: NavRoute,
        source: NavigationSource?,
    ) {
        emit(NavSignal.GoToDestPage(route, source))
    }

    override fun navigateReplace(
        page: Page,
        source: NavigationSource?,
    ) {
        emit(NavSignal.Replace(page.toRoute(), source))
    }

    override fun navigateDeepLink(route: NavRoute) {
        emit(NavSignal.DeepLink(route))
    }

    override fun navigateToBack() {
        emit(NavSignal.Back)
    }

    override fun finishAuthentication() {
        emit(NavSignal.AuthenticationCompleted)
    }

    private fun emit(navSignal: NavSignal) {
        val result = _navigationFlow.trySend(navSignal)
        if (result.isFailure) Log.w("NavigationHelper", "dropped: $navSignal")
    }
}

val LocalNavigationHelper = compositionLocalOf<NavigationHelper> { error("No user found!") }
