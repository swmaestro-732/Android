package com.chillsam.courmy.main.presentation.navigation

import com.chillsam.courmy.common.domain.navigation.NavSignal
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.ProductEvent
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.entity.category.CourseTagVO

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
            from?.args?.analyticsArguments()?.forEach { (key, value) ->
                put("from_$key", value)
            }
            to.args.analyticsArguments().forEach { (key, value) -> put("to_$key", value) }
        },
    )
}

private fun Map<String, String>.analyticsArguments(): Map<String, String> =
    mapNotNull { (key, value) -> validatedArgument(key, value)?.let { key to it } }.toMap()

private fun validatedArgument(
    key: String,
    value: String,
): String? =
    when (key) {
        "courseId", "placeId", "planId" -> value.toLongOrNull()?.takeIf { it > 0 }?.toString()
        "theme", "category" -> CourseTagVO.fromCodeOrLabel(value)?.code
        "tab" -> value.takeIf { it in setOf("plans", "follower", "following") }
        "target" -> value.takeIf { it in setOf("course", "place") }
        "section" -> value.takeIf { it == "completedCourses" }
        "sido" -> value.takeIf { it in SIDO_CODES }
        else -> null
    }

private val SIDO_CODES =
    setOf("11", "26", "27", "28", "29", "30", "31", "36", "41", "43", "44", "46", "47", "48", "50", "51", "52")

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
