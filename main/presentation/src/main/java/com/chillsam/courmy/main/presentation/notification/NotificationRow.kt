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

@Composable
internal fun NotificationRow(
    item: NotificationItemVO,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        Modifier
            .fillMaxWidth()
            .background(if (item.isRead) color.bgDefaultLevel0 else color.bgAccentSubtle)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { stateDescription = if (item.isRead) "읽음" else "안 읽음" },
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

internal fun notificationMessage(item: NotificationItemVO): String {
    val name = item.actorNickname.ifBlank { "누군가" }
    return when (item.type) {
        "COURSE_LIKED" -> "${name}님이 내 코스를 좋아해요"
        "COURSE_COMMENTED" -> "${name}님이 내 코스에 댓글을 남겼어요"
        "COURSE_TRACE_COMPLETED" -> "${name}님이 내 코스를 따라갔어요"
        "COURSE_REVIEWED" -> "${name}님이 내 코스에 후기를 남겼어요"
        "COURSE_BOOKMARKED" -> "${name}님이 내 코스를 저장했어요"
        "USER_FOLLOWED" -> "${name}님이 나를 팔로우해요"
        else -> "새로운 소식이 도착했어요"
    }
}

private fun notificationIcon(type: String): Int =
    when (type) {
        "COURSE_LIKED" -> R.drawable.ic_heart_24
        "COURSE_COMMENTED" -> R.drawable.ic_review_bubble_24
        "COURSE_TRACE_COMPLETED" -> R.drawable.ic_walk_24
        "COURSE_REVIEWED" -> R.drawable.ic_review_write_24
        "COURSE_BOOKMARKED" -> R.drawable.ic_bookmark_filled_24
        "USER_FOLLOWED" -> R.drawable.ic_tab_person_24
        else -> R.drawable.ic_notifications_24
    }

internal fun notificationTime(
    value: String,
    now: Instant = Instant.now(),
): String {
    val date = runCatching { Instant.parse(value) }.getOrNull() ?: return ""
    val minutes = Duration.between(date, now).toMinutes().coerceAtLeast(0)
    return when {
        minutes < 1 -> "방금 전"
        minutes < 60 -> "${minutes}분 전"
        minutes < 1440 -> "${minutes / 60}시간 전"
        else -> DateTimeFormatter.ofPattern("M월 d일").withZone(ZoneId.systemDefault()).format(date)
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
