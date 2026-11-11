package com.chillsam.courmy.main.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.buttonClick
import com.chillsam.courmy.common.domain.telemetry.selectionChange
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.telemetry.LocalTelemetry
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.my.ProfileEditPage
import com.chillsam.courmy.main.presentation.R
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.domain.settings.AccountManagePage as AccountManageRoute
import com.chillsam.courmy.main.domain.settings.AppVersionPage as AppVersionRoute

/** 문의하기: 서비스 이용 문의·오류 신고·계정 문제·게시물 신고·제휴 제안을 받는 구글 폼. */
private const val CONTACT_FORM_URL =
    "https://docs.google.com/forms/d/e/1FAIpQLScCymWkAL7w9L_ykCRBelKvXFBZu1WwCG9nLgl2w19qwDX9Ew/viewform"

/** 개인정보 보호: 개인정보 처리방침 노션 문서. */
private const val PRIVACY_POLICY_URL =
    "https://brook-insect-146.notion.site/3b25d2e08a5c80fc8bade8eefd0ddbb9"

/**
 * 설정 화면(FS-28).
 * 유형별 섹션 구분 없이 항목 하나하나를 목록 행으로 두고, 행 사이만 구분선으로 나눈다.
 */
@Composable
fun SettingsPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val telemetry = LocalTelemetry.current
    // 비로그인(게스트)에게는 로그인해야 쓸 수 있는 항목(프로필·계정·알림 동의)을 숨긴다.
    // 언어·문의·개인정보·앱 버전은 로그인 없이 쓰므로 그대로 노출한다.
    val loggedIn = LocalSessionUiState.current.isLoggedIn
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val notReadyMessage = stringResource(R.string.settings_toast_not_ready)
    val noBrowserMessage = stringResource(R.string.settings_toast_no_browser)
    // 공지는 아직 상세 화면이 없어, 무반응 대신 "준비 중" 안내를 띄운다.
    val notReady = { Toast.makeText(context, notReadyMessage, Toast.LENGTH_SHORT).show() }
    // 브라우저가 없는 기기에서 uriHandler 가 던지면 앱이 죽으므로 안내로 대체한다.
    val openUrl = { url: String ->
        runCatching { uriHandler.openUri(url) }
            .onFailure { Toast.makeText(context, noBrowserMessage, Toast.LENGTH_SHORT).show() }
        Unit
    }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(
            title = stringResource(R.string.settings_title),
            onBack = { navigationHelper.navigateToBack() },
            boxed = true,
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
        ) {
            if (loggedIn) {
                SettingsMenuRow(label = stringResource(R.string.settings_profile_edit), onClick = {
                    navigationHelper.navigateTo(ProfileEditPage, source = NavigationSource("/settings", "edit_profile"))
                })
                SettingsDivider()
                SettingsMenuRow(label = stringResource(R.string.settings_account), onClick = {
                    navigationHelper.navigateTo(AccountManageRoute, source = NavigationSource("/settings", "account"))
                })
                SettingsDivider()
            }
            SettingsMenuRow(
                label = stringResource(R.string.settings_notice),
                onClick = notReady,
                hint = stringResource(R.string.settings_hint_not_ready),
            )
            SettingsDivider()
            if (loggedIn) {
                SettingsMenuRow(
                    label = stringResource(R.string.settings_notification),
                    onClick = {
                        navigationHelper.navigateTo(
                            com.chillsam.courmy.main.domain.settings.NotificationSettingsPage,
                            source = NavigationSource("/settings", "notifications"),
                        )
                    },
                )
                SettingsDivider()
            }
            SettingsMenuRow(
                label = stringResource(R.string.settings_language),
                onClick = {
                    navigationHelper.navigateTo(
                        com.chillsam.courmy.main.domain.settings.LanguageSettingsPage,
                        source = NavigationSource("/settings", "language"),
                    )
                },
            )
            SettingsDivider()
            SettingsMenuRow(label = stringResource(R.string.settings_contact), onClick = {
                telemetry.buttonClick("/settings", "contact", mapOf("destination" to "external_browser"))
                openUrl(CONTACT_FORM_URL)
            })
            SettingsDivider()
            SettingsMenuRow(label = stringResource(R.string.settings_privacy), onClick = {
                telemetry.buttonClick("/settings", "privacy_policy", mapOf("destination" to "external_browser"))
                openUrl(PRIVACY_POLICY_URL)
            })
            SettingsDivider()
            SettingsMenuRow(label = stringResource(R.string.settings_app_version), onClick = {
                navigationHelper.navigateTo(AppVersionRoute, source = NavigationSource("/settings", "app_version"))
            })
        }
    }
}
