package com.chillsam.courmy.main.presentation.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.buttonClick
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.telemetry.LocalTelemetry
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.domain.login.ProfileSetupPage

/**
 * 약관 동의 화면(FS-04). 필수 3개 항목을 동의하고 프로필 설정으로 이어진다.
 * 필수 항목을 모두 체크해야 "동의하고 계속하기"가 활성화된다.
 */
@Composable
fun TermsAgreementPage(modifier: Modifier = Modifier) {
    // 상단이 흰색이라 상태바도 같은 색으로 이어 붙인다. 회색으로 두면 띠만 분리돼 보인다.
    StatusBarColor(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
    val telemetry = LocalTelemetry.current
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor

    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val age14 = SignupSelectionStore.ageConfirmed
    val tos = SignupSelectionStore.termsAgreed
    val privacy = SignupSelectionStore.privacyAgreed
    val requiredAll = SignupSelectionStore.requiredTermsAgreed
    val openDocument: (String) -> Unit = { url ->
        telemetry.buttonClick(
            "/login/terms",
            if (url ==
                SERVICE_TERMS_URL
            ) {
                "view_terms"
            } else {
                "view_privacy"
            },
            mapOf("destination" to "external_browser"),
        )
        runCatching { uriHandler.openUri(url) }.onFailure {
            Toast.makeText(context, "문서를 열 수 없어요. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel1).statusBarsPadding()) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = ScreenHorizontalPadding),
        ) {
            Spacer(Modifier.height(16.dp))
            LoggedInBadge()
            DsText(
                text = "약관에\n동의해 주세요",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                maxLines = 2,
                modifier = Modifier.padding(top = 16.dp),
            )
            DsText(
                text = "서비스 이용을 위해 아래 약관 동의가 필요해요.",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(Modifier.weight(1f))

            AgreeRow(
                text = "전체 동의",
                checked = requiredAll,
                emphasize = true,
                onToggle = {
                    val v = !requiredAll
                    SignupSelectionStore.ageConfirmed = v
                    SignupSelectionStore.termsAgreed = v
                    SignupSelectionStore.privacyAgreed = v
                },
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = color.borderDefaultLevel0,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            AgreeRow(
                text = "[필수] 만 14세 이상입니다",
                checked = age14,
                onToggle = { SignupSelectionStore.ageConfirmed = !age14 },
            )
            AgreeRow(text = "[필수] 서비스 이용약관 동의", checked = tos, onView = {
                openDocument(
                    SERVICE_TERMS_URL,
                )
            }, onToggle = {
                SignupSelectionStore.termsAgreed =
                    !tos
            })
            AgreeRow(
                text = "[필수] 개인정보 처리방침 동의",
                checked = privacy,
                onView = { openDocument(PRIVACY_POLICY_URL) },
                onToggle = { SignupSelectionStore.privacyAgreed = !privacy },
            )
            Spacer(Modifier.height(12.dp))
        }
        Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
            DsButton(
                text = "동의하고 계속하기",
                enabled = requiredAll,
                onClick = {
                    telemetry.buttonClick("/login/terms", "agree_continue")
                    navigationHelper.navigateTo(
                        ProfileSetupPage,
                        source = NavigationSource("/login/terms", "agree_continue"),
                    )
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
            )
        }
    }
}

/** 상단 "소셜 계정 확인 완료" 배지(소셜 로그인 완료 표시). */
@Composable
private fun LoggedInBadge() {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(color.bgAccentSubtle)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color.contentAccent))
        DsText(
            text = "소셜 계정 확인 완료",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel1,
        )
    }
}

@Composable
private fun AgreeRow(
    text: String,
    checked: Boolean,
    onToggle: () -> Unit,
    emphasize: Boolean = false,
    onView: (() -> Unit)? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (checked) color.bgAccent else color.bgDefaultLevel0),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check_24),
                contentDescription = null,
                tint = if (checked) color.contentOnAccent else color.contentDefaultLevel3,
                modifier = Modifier.size(15.dp),
            )
        }
        DsText(
            text = text,
            style =
                if (emphasize) {
                    DesignSystemThemeImpl.typeScale.textStrongS
                } else {
                    DesignSystemThemeImpl.typeScale.textRegularS
                },
            color = color.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        if (onView != null) {
            DsText(
                text = "보기 ›",
                modifier = Modifier.clickable(onClick = onView).padding(8.dp),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
        }
    }
}

internal const val SERVICE_TERMS_URL =
    "https://brook-insect-146.notion.site/3b25d2e08a5c80ce9d6dfb98d6de2b07"
internal const val PRIVACY_POLICY_URL =
    "https://brook-insect-146.notion.site/3b25d2e08a5c80fc8bade8eefd0ddbb9"
