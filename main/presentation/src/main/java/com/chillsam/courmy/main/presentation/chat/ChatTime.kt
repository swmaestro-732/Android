package com.chillsam.courmy.main.presentation.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.chillsam.courmy.main.presentation.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Display time stays semantic so changing the app language also changes existing messages. */
sealed interface ChatTime {
    data object Empty : ChatTime

    data object Now : ChatTime

    data class DaysAgo(
        val days: Int,
    ) : ChatTime

    data class Clock(
        val hour: Int,
        val minute: Int,
    ) : ChatTime
}

@Composable
internal fun ChatTime.label(): String =
    when (this) {
        ChatTime.Empty -> {
            ""
        }

        ChatTime.Now -> {
            stringResource(R.string.main_remaining_chat_now)
        }

        is ChatTime.DaysAgo -> {
            if (days == 1) {
                stringResource(R.string.main_remaining_chat_yesterday)
            } else {
                pluralStringResource(R.plurals.main_remaining_chat_days, days, days)
            }
        }

        is ChatTime.Clock -> {
            DateTimeFormatter
                .ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(LocalConfiguration.current.locales[0])
                .format(LocalTime.of(hour, minute))
        }
    }
