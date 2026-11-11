package com.chillsam.courmy.common.presentation.translation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.chillsam.courmy.common.entity.time.RelativeTimeUnit
import com.chillsam.courmy.common.entity.time.relativeTime
import com.chillsam.courmy.common.presentation.R

@Composable
fun relativeTimeText(epochMillis: Long?): String {
    if (epochMillis == null) return ""
    val time = relativeTime(epochMillis)
    val resource =
        when (time.unit) {
            RelativeTimeUnit.JUST_NOW -> return stringResource(R.string.relative_just_now)
            RelativeTimeUnit.MINUTE -> R.plurals.relative_minute
            RelativeTimeUnit.HOUR -> R.plurals.relative_hour
            RelativeTimeUnit.DAY -> R.plurals.relative_day
            RelativeTimeUnit.WEEK -> R.plurals.relative_week
            RelativeTimeUnit.MONTH -> R.plurals.relative_month
            RelativeTimeUnit.YEAR -> R.plurals.relative_year
        }
    return pluralStringResource(resource, time.count.toInt(), time.count)
}
