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
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.my.ProfileEditPage
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.domain.settings.AccountManagePage as AccountManageRoute

/** 문의하기: 서비스 이용 문의·오류 신고·계정 문제·게시물 신고·제휴 제안을 받는 구글 폼. */
private const val CONTACT_FORM_URL =
    "https://docs.google.com/forms/d/e/1FAIpQLScCymWkAL7w9L_ykCRBelKvXFBZu1WwCG9nLgl2w19qwDX9Ew/viewform"

/** 개인정보 보호: 개인정보 처리방침 노션 문서. */
private const val PRIVACY_POLICY_URL =
    "https://brook-insect-146.notion.site/3b25d2e08a5c80fc8bade8eefd0ddbb9"

/** 상세 화면이 아직 없는 항목에 붙는 표시. */
private const val NOT_READY_HINT = "(준비 중)"

/**
 * 설정 화면(FS-28).
 * 유형별 섹션 구분 없이 항목 하나하나를 목록 행으로 두고, 행 사이만 구분선으로 나눈다.
 */
@Composable
fun SettingsPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    // 공지·알림은 아직 상세 화면이 없어, 무반응 대신 "준비 중" 안내를 띄운다.
    val notReady = { Toast.makeText(context, "준비 중이에요", Toast.LENGTH_SHORT).show() }
    // 브라우저가 없는 기기에서 uriHandler 가 던지면 앱이 죽으므로 안내로 대체한다.
    val openUrl = { url: String ->
        runCatching { uriHandler.openUri(url) }
            .onFailure { Toast.makeText(context, "브라우저를 열 수 없어요", Toast.LENGTH_SHORT).show() }
        Unit
    }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(title = "설정", onBack = { navigationHelper.navigateToBack() }, boxed = true)

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
        ) {
            SettingsMenuRow(label = "프로필 수정", onClick = { navigationHelper.navigateTo(ProfileEditPage) })
            SettingsDivider()
            SettingsMenuRow(label = "계정 관리", onClick = { navigationHelper.navigateTo(AccountManageRoute) })
            SettingsDivider()
            SettingsMenuRow(label = "공지", onClick = notReady, hint = NOT_READY_HINT)
            SettingsDivider()
            SettingsMenuRow(label = "알림", onClick = notReady, hint = NOT_READY_HINT)
            SettingsDivider()
            SettingsMenuRow(label = "문의하기", onClick = { openUrl(CONTACT_FORM_URL) })
            SettingsDivider()
            SettingsMenuRow(label = "개인정보 보호", onClick = { openUrl(PRIVACY_POLICY_URL) })
        }
    }
}
