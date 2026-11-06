package com.chillsam.courmy.main.presentation.notification

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationManagerCompat
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
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
 * 확인 버튼으로만 끝난다. 확인하면 동의 저장에 이어 **OS 알림 설정 획득까지** 진행한다:
 * 권한이 없으면 요청(Android 13+)하고 답을 받으면 결과와 상관없이 닫는다(거절 직후에
 * 기기 설정 안내를 겹쳐 띄우지 않는다). 요청 자체가 불가능한 상태(이전 거절·차단)면
 * 기기 설정 안내를 한 번 띄운 뒤 닫는다. (시작 시 자동 요청은 동의 전에는 건너뛴다 — MainActivity)
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
            // 방금 묻고 답을 받았으므로 결과와 상관없이 조용히 닫는다. 거절한 사용자에게
            // 곧바로 기기 설정 안내를 또 띄우지 않는다 — 이후 변경은 설정 > 알림이 맡는다.
            onIntent(NotificationConsentIntent.Finish)
        }
    // 동의 저장이 끝나면 OS 알림 설정 획득 단계로 넘어간다. 시트는 이 단계가 끝나야 닫힌다.
    LaunchedEffect(state.consentDone) {
        if (!state.consentDone) return@LaunchedEffect
        val notifications = NotificationManagerCompat.from(context)
        when {
            notifications.canShowActivityNotifications() -> {
                onIntent(NotificationConsentIntent.Finish)
            }

            notifications.canRequestNotificationPermission(
                requested = permissionPreferences.getBoolean("requested", false),
            ) -> {
                // 시작 시 자동 요청과 같은 키를 써서 어느 쪽이든 설치당 한 번만 묻는다.
                permissionPreferences.edit().putBoolean("requested", true).apply()
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            else -> {
                // 프롬프트를 띄울 수 없는 상태(이전 거절·차단·12 이하에서 OS 꺼짐)에서 방금
                // 알림을 받겠다고 동의했으므로, 유일한 수단인 기기 설정으로 안내한다.
                onIntent(NotificationConsentIntent.PermissionBlocked)
            }
        }
    }
    NotificationConsentContent(
        state = state,
        onIntent = onIntent,
        onConfirm = { onIntent(NotificationConsentIntent.Confirm) },
    )
    if (state.permissionGuideVisible) {
        DsConfirmDialog(
            title = "기기 알림을 허용해 주세요",
            description = "활동 알림을 받으려면 기기 설정에서 Courmy 알림을 허용해 주세요.",
            confirmText = "기기 설정",
            dismissText = "닫기",
            onConfirm = {
                context.openNotificationSettings()
                onIntent(NotificationConsentIntent.Finish)
            },
            onDismiss = { onIntent(NotificationConsentIntent.Finish) },
        )
    }
}

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
                    NotificationToggleRow(
                        title = "활동 알림",
                        tag = "(필수)",
                        description = "내 코스의 좋아요와 댓글 소식을 받아요",
                        checked = state.serviceAgreed,
                        onToggle = { onIntent(NotificationConsentIntent.ToggleService) },
                    )
                    NotificationToggleRow(
                        title = "광고 알림",
                        tag = "(선택)",
                        description = "추천 코스와 이벤트 소식을 받아요",
                        checked = state.marketingAgreed,
                        onToggle = { onIntent(NotificationConsentIntent.ToggleMarketing) },
                    )
                    // 야간을 먼저 켜면 광고 동의가 함께 켜진다 — 토글을 흐리게 두지 않고 모양을 통일한다.
                    NotificationToggleRow(
                        title = "야간에도 받기",
                        tag = "(선택)",
                        description = "밤 9시부터 오전 8시까지도 받아요",
                        checked = state.nightAgreed,
                        onToggle = { onIntent(NotificationConsentIntent.ToggleNight) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DsButton(
                        text = "동의하고 계속하기",
                        enabled = state.confirmEnabled,
                        loading = state.saving || state.consentDone,
                        onClick = onConfirm,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
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
