package com.chillsam.courmy.main.presentation.notification

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationManagerCompat
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsSwitch
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 뒤 화면을 덮는 스크림 농도. [DsConfirmDialog] 와 동일값. */
private const val SCRIM_ALPHA = 0.4f

private val SheetCornerRadius = 20.dp
private val HandleWidth = 36.dp
private val HandleHeight = 4.dp

/**
 * 홈 최초 진입 알림 수신 동의 팝업(SCRUM-725). 하단 시트 형태의 전면 Dialog 다.
 *
 * 활동 알림(필수)에 동의해야 확인할 수 있어 바깥 탭·뒤로가기로는 닫히지 않고
 * 확인 버튼으로만 끝난다. 확인 직후 Android 13+ 라면 OS 알림 권한을 이어서 요청한다
 * (시작 시 자동 요청은 동의 전에는 건너뛴다 — MainActivity).
 */
@Composable
fun NotificationConsentSheet(
    state: NotificationConsentUIState,
    onIntent: (NotificationConsentIntent) -> Unit,
) {
    val context = LocalContext.current
    val permissionPreferences =
        remember(context) { context.getSharedPreferences("notification_permission", Context.MODE_PRIVATE) }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            // 허용/거절과 관계없이 흐름을 계속한다. 설정 화면이 OS 상태를 다시 읽는다.
        }
    NotificationConsentContent(
        state = state,
        onIntent = onIntent,
        onConfirm = {
            onIntent(NotificationConsentIntent.Confirm)
            // 활동 알림(필수)에 동의한 직후라 알림 표시에 필요한 OS 권한을 바로 잇는다.
            if (canRequestNotificationPermission(context, permissionPreferences.getBoolean("requested", false))) {
                // 시작 시 자동 요청과 같은 키를 써서 어느 쪽이든 설치당 한 번만 묻는다.
                permissionPreferences.edit().putBoolean("requested", true).apply()
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
    )
}

private fun canRequestNotificationPermission(
    context: Context,
    alreadyRequested: Boolean,
): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        !NotificationManagerCompat.from(context).areNotificationsEnabled() &&
        !alreadyRequested

@Composable
private fun NotificationConsentContent(
    state: NotificationConsentUIState,
    onIntent: (NotificationConsentIntent) -> Unit,
    onConfirm: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Dialog(
        // 필수 동의 전에는 닫을 수 없다. 확인 버튼이 유일한 출구다.
        onDismissRequest = {},
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = SCRIM_ALPHA)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = SheetCornerRadius, topEnd = SheetCornerRadius))
                        .background(color.bgDefaultLevel1)
                        .padding(horizontal = 20.dp)
                        .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier =
                        Modifier
                            .size(width = HandleWidth, height = HandleHeight)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(color.bgSwitchOff),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    DsText(
                        text = "추천 코스 알림을 받아볼까요?",
                        style = DesignSystemThemeImpl.typeScale.textStrongM,
                        color = color.contentDefaultLevel0,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    DsText(
                        text = "나중에 설정에서 바꿀 수 있어요.",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel1,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ConsentRow(
                        title = "활동 알림",
                        tag = "(필수)",
                        description = "내 코스의 좋아요와 댓글 소식을 받아요",
                        checked = state.serviceAgreed,
                        onToggle = { onIntent(NotificationConsentIntent.ToggleService) },
                    )
                    ConsentRow(
                        title = "광고 알림",
                        tag = "(선택)",
                        description = "추천 코스와 이벤트 소식을 받아요",
                        checked = state.marketingAgreed,
                        onToggle = { onIntent(NotificationConsentIntent.ToggleMarketing) },
                    )
                    ConsentRow(
                        title = "야간에도 받기",
                        tag = "(선택)",
                        description = "밤 9시부터 오전 8시까지도 받아요",
                        checked = state.nightAgreed,
                        onToggle = { onIntent(NotificationConsentIntent.ToggleNight) },
                        enabled = state.marketingAgreed,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DsButton(
                        text = "동의하고 계속하기",
                        enabled = state.confirmEnabled,
                        loading = state.saving,
                        onClick = onConfirm,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ConsentRow(
    title: String,
    tag: String,
    description: String,
    checked: Boolean,
    onToggle: () -> Unit,
    enabled: Boolean = true,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .toggleable(
                    value = checked,
                    enabled = enabled,
                    role = Role.Switch,
                    onValueChange = { onToggle() },
                ).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DsText(
                    text = title,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                )
                Spacer(modifier = Modifier.width(4.dp))
                // 필수/선택 구분은 라벨보다 한 단계 가볍게 쓴다.
                DsText(
                    text = tag,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel1,
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            DsText(
                text = description,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        // 행 전체가 토글이므로 스위치의 중복 접근성 노드는 지운다(설정 > 알림과 동일).
        DsSwitch(
            checked = checked,
            onCheckedChange = { onToggle() },
            enabled = enabled,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationConsentContentPreview() {
    DesignSystemTheme {
        NotificationConsentContent(
            state =
                NotificationConsentUIState(
                    visible = true,
                    marketingAgreed = true,
                    nightAgreed = true,
                ),
            onIntent = {},
            onConfirm = {},
        )
    }
}
