package com.chillsam.courmy.main.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.notification.NotificationItemVO
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.chillsam.courmy.main.presentation.R as MainR

@Composable
internal fun NotificationRow(
    item: NotificationItemVO,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val stateRead = stringResource(MainR.string.main_notification_state_read)
    val stateUnread = stringResource(MainR.string.main_notification_state_unread)
    Column(
        Modifier
            .fillMaxWidth()
            .background(if (item.isRead) color.bgDefaultLevel0 else color.bgAccentSubtle)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { stateDescription = if (item.isRead) stateRead else stateUnread },
    ) {
        Row(
            Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(Modifier.size(44.dp).background(color.bgDefaultLevel1, CircleShape), Alignment.Center) {
                Icon(
                    painterResource(notificationIcon(item.type)),
                    null,
                    tint = color.contentAccent,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DsText(
                    notificationMessage(item),
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                    maxLines = Int.MAX_VALUE,
                )
                item.courseTitle?.takeIf { it.isNotBlank() }?.let { title ->
                    DsText(
                        title,
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel2,
                        maxLines = 2,
                    )
                }
                DsText(
                    notificationTime(item.updatedAt),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                )
            }
            if (!item.isRead) Box(Modifier.padding(top = 7.dp).size(6.dp).background(color.contentAccent, CircleShape))
        }
        HorizontalDivider(Modifier.padding(start = 82.dp, end = 24.dp), color = color.borderDefaultLevel0)
    }
}

@Composable
internal fun notificationMessage(item: NotificationItemVO): String {
    val name = item.actorNickname.ifBlank { stringResource(MainR.string.main_remaining_actor_unknown) }
    return when (item.type) {
        "COURSE_LIKED" -> stringResource(MainR.string.main_remaining_notification_liked, name)
        "COURSE_COMMENTED" -> stringResource(MainR.string.main_remaining_notification_commented, name)
        "COURSE_REVIEWED" -> stringResource(MainR.string.main_remaining_notification_reviewed, name)
        "USER_FOLLOWED" -> stringResource(MainR.string.main_remaining_notification_followed, name)
        else -> stringResource(MainR.string.main_remaining_notification_unknown)
    }
}

private fun notificationIcon(type: String): Int =
    when (type) {
        "COURSE_LIKED" -> R.drawable.ic_heart_24
        "COURSE_COMMENTED" -> R.drawable.ic_review_bubble_24
        "COURSE_REVIEWED" -> R.drawable.ic_review_write_24
        "USER_FOLLOWED" -> R.drawable.ic_tab_person_24
        else -> R.drawable.ic_notifications_24
    }

@Composable
internal fun notificationTime(
    value: String,
    now: Instant = Instant.now(),
): String {
    val date = runCatching { Instant.parse(value) }.getOrNull() ?: return ""
    val minutes = Duration.between(date, now).toMinutes().coerceAtLeast(0)
    return when {
        minutes < 1 -> {
            stringResource(MainR.string.main_remaining_notification_now)
        }

        minutes < 60 -> {
            pluralStringResource(MainR.plurals.main_remaining_notification_minutes, minutes.toInt(), minutes)
        }

        minutes < 1440 -> {
            pluralStringResource(
                MainR.plurals.main_remaining_notification_hours,
                (minutes / 60).toInt(),
                minutes / 60,
            )
        }

        else -> {
            DateTimeFormatter
                .ofPattern(
                    stringResource(MainR.string.main_remaining_notification_date_pattern),
                    androidx.compose.ui.platform.LocalConfiguration.current.locales[0],
                ).withZone(ZoneId.systemDefault())
                .format(date)
        }
    }
}

@Composable
internal fun InboxEmpty(
    title: String,
    description: String,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(Modifier.size(72.dp).clip(RoundedCornerShape(24.dp)).background(color.bgAccentSubtle), Alignment.Center) {
            Icon(
                painterResource(R.drawable.ic_notifications_24),
                null,
                tint = color.contentAccent,
                modifier = Modifier.size(32.dp),
            )
        }
        DsText(title, style = DesignSystemThemeImpl.typeScale.textStrongM, color = color.contentDefaultLevel0)
        DsText(
            description,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
            maxLines = Int.MAX_VALUE,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        if (action !=
            null
        ) {
            TextButton(onClick = onAction) { DsText(action, style = DesignSystemThemeImpl.typeScale.textStrongS) }
        }
    }
}
