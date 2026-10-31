package com.chillsam.courmy.main.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

@Composable
fun NotificationBellButton(
    unreadCount: Long?,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(Modifier.size(48.dp).clip(CircleShape).clickable(role = Role.Button, onClick = onClick), Alignment.Center) {
        Box(Modifier.size(34.dp).background(color.bgAccent, CircleShape), Alignment.Center) {
            Icon(
                painterResource(R.drawable.ic_notifications_24),
                contentDescription =
                    if (unreadCount != null &&
                        unreadCount > 0
                    ) {
                        "알림, 안 읽은 알림 ${unreadCount}개"
                    } else {
                        "알림"
                    },
                tint = color.contentOnAccent,
                modifier = Modifier.size(19.dp),
            )
        }
        if (unreadCount != null && unreadCount > 0) {
            Box(
                Modifier.align(Alignment.TopEnd).size(21.dp).background(color.bgDefaultLevel1, CircleShape),
                Alignment.Center,
            ) {
                DsText(
                    if (unreadCount > 99) "99+" else unreadCount.toString(),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentAccent,
                )
            }
        }
    }
}
