package com.chillsam.courmy.course.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.presentation.translation.LocalAppLanguage
import com.chillsam.courmy.common.presentation.translation.rememberAutoTranslated
import com.chillsam.courmy.course.presentation.R

// Only a leading weekday token is structural; free-form notes still use on-device translation.
private val weekdayPrefix = Regex("^([월화수목금토일])(?:요일)?(?=\\s|$)")

@Composable
internal fun openingHoursText(line: String): String {
    if (LocalAppLanguage.current == AppLanguage.KOREAN) return line
    val source = line.trim()
    val weekday = weekdayPrefix.find(source)
    val prefix =
        weekday
            ?.let {
                stringArrayResource(R.array.opening_hours_weekdays)["월화수목금토일".indexOf(it.groupValues[1])]
            }.orEmpty()
    val remainder = source.substring(weekday?.range?.last?.plus(1) ?: 0).trim()
    val body =
        when (remainder) {
            "휴무" -> {
                stringResource(R.string.opening_hours_closed)
            }

            "정기휴무", "정기 휴무" -> {
                stringResource(R.string.opening_hours_regular_closed)
            }

            "연중무휴" -> {
                stringResource(R.string.opening_hours_always_open)
            }

            else -> {
                val daily = remainder.startsWith("매일 ")
                val translated =
                    rememberAutoTranslated(remainder.removePrefix(if (daily) "매일 " else ""), enabled = true)
                if (daily) "${stringResource(R.string.opening_hours_daily)} $translated" else translated
            }
        }
    return listOf(prefix, body).filter { it.isNotBlank() }.joinToString(" ")
}
