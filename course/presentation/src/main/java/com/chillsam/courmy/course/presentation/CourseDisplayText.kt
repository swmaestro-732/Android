package com.chillsam.courmy.course.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import java.util.Locale
import com.chillsam.courmy.common.presentation.R as CommonR

@Composable
internal fun placeCountText(count: Int): String = pluralStringResource(R.plurals.course_remaining_places, count, count)

@Composable
internal fun reviewCountText(count: Int): String =
    pluralStringResource(R.plurals.course_remaining_reviews, count, count)

@Composable
internal fun walkingText(minutes: Int?): String =
    when {
        minutes == null -> ""
        minutes < 0 -> stringResource(CommonR.string.message_walking_unreachable)
        else -> stringResource(CommonR.string.message_walking_minutes, minutes)
    }

@Composable
internal fun followerText(count: Int): String {
    val compact =
        when {
            count < 1000 -> count.toString()
            count % 1000 == 0 -> "${count / 1000}k"
            else -> String.format(Locale.US, "%.1fk", (count / 100) / 10.0)
        }
    return stringResource(R.string.course_remaining_followers, compact)
}
