package com.chillsam.courmy.main.presentation.navigation

import com.chillsam.courmy.common.domain.navigation.NavSignal
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.ProductEvent
import com.chillsam.courmy.common.domain.telemetry.Telemetry

/** route args 전체를 보내지 않는다: 검색어·프로필 handle·좌표는 여기서 수집하지 않는다. */
internal fun Telemetry.recordNavigation(
    from: GenericNavKey?,
    to: GenericNavKey?,
    action: String,
    source: NavigationSource? = null,
) {
    if (from == to || to == null) return
    logEvent(
        ProductEvent.Navigation,
        buildMap {
            put("from_screen", from?.path ?: "app_start")
            put("to_screen", to.path)
            put("action", action)
            source?.let {
                put("source_screen", it.screen)
                put("trigger", it.trigger)
                put(if (it.trigger == "button") "button" else "reason", it.button)
            }
            from?.args?.filterKeys { it in NAVIGATION_PROPERTY_KEYS }?.forEach { (key, value) ->
                put("from_$key", value)
            }
            to.args.filterKeys { it in NAVIGATION_PROPERTY_KEYS }.forEach { (key, value) -> put("to_$key", value) }
        },
    )
}

private val NAVIGATION_PROPERTY_KEYS =
    setOf("courseId", "placeId", "planId", "theme", "category", "tab", "target", "section", "sido")

internal val NavSignal.analyticsAction: String
    get() =
        when (this) {
            is NavSignal.GoToDestPage -> "push"
            is NavSignal.DeepLink -> "deep_link"
            is NavSignal.Replace -> "replace"
            NavSignal.Back -> "app_back"
            NavSignal.AuthenticationCompleted -> "authentication_completed"
        }

internal val NavSignal.analyticsSource: NavigationSource?
    get() =
        when (this) {
            is NavSignal.GoToDestPage -> source
            is NavSignal.DeepLink -> source
            is NavSignal.Replace -> source
            else -> null
        }
