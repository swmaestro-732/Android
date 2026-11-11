package com.chillsam.courmy.main.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsDialogScaffold
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.R as MainR

/**
 * OS 알림 권한(POST_NOTIFICATIONS) 요청 직전에 띄우는 Courmy 사전 안내(프라이밍) 다이얼로그.
 *
 * 시스템 권한 다이얼로그는 OS 가 기기 테마로 그려 앱이 색·모양을 바꿀 수 없다. 그래서 설정 > 알림에서
 * 활동 알림을 켤 때, 시스템 프롬프트 전에 Courmy 색·형태의 이 다이얼로그로 맥락을 먼저 안내한다.
 * [onAllow] 를 누른 경우에만 실제 시스템 프롬프트를 띄우고, [onDismiss](나중에) 면 프롬프트를 띄우지 않아
 * 설치당 1회뿐인 시스템 프롬프트를 아껴 둔다(홈 최초 진입 팝업은 동의 바텀시트가 같은 역할을 한다).
 */
@Composable
internal fun NotificationPermissionPrimingDialog(
    onAllow: () -> Unit,
    onDismiss: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    DsDialogScaffold(
        title = stringResource(MainR.string.main_notification_priming_title),
        description = stringResource(MainR.string.main_notification_priming_description),
        onDismiss = onDismiss,
        icon = {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(color.bgAccent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notifications_24),
                    contentDescription = null,
                    tint = color.contentOnAccent,
                    modifier = Modifier.size(28.dp),
                )
            }
        },
    ) {
        DsButton(
            text = stringResource(MainR.string.main_notification_priming_allow),
            variant = DsButtonVariant.Primary,
            onClick = onAllow,
        )
        DsButton(
            text = stringResource(MainR.string.main_notification_priming_later),
            variant = DsButtonVariant.Secondary,
            onClick = onDismiss,
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun NotificationPermissionPrimingDialogPreview() {
    DesignSystemTheme {
        NotificationPermissionPrimingDialog(onAllow = {}, onDismiss = {})
    }
}
