package com.chillsam.courmy.main.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.domain.login.ProfileSetupPage

/**
 * 약관 동의 화면(FS-04, Figma 상 "보류"). 필수 3 + 선택 1 약관을 동의하고 프로필 설정으로 이어진다.
 * 필수 항목을 모두 체크해야 "동의하고 계속하기"가 활성화된다.
 */
@Composable
fun TermsAgreementPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor

    var age14 by remember { mutableStateOf(false) }
    var tos by remember { mutableStateOf(false) }
    var privacy by remember { mutableStateOf(false) }
    var marketing by remember { mutableStateOf(false) }
    val requiredAll = age14 && tos && privacy
    val allChecked = requiredAll && marketing

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
                checked = allChecked,
                emphasize = true,
                onToggle = {
                    val v = !allChecked
                    age14 = v
                    tos = v
                    privacy = v
                    marketing = v
                },
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = color.borderDefaultLevel0,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            AgreeRow(text = "[필수] 만 14세 이상입니다", checked = age14, onToggle = { age14 = !age14 })
            AgreeRow(text = "[필수] 서비스 이용약관 동의", checked = tos, showView = true, onToggle = { tos = !tos })
            AgreeRow(
                text = "[필수] 개인정보 수집·이용 동의",
                checked = privacy,
                showView = true,
                onToggle = { privacy = !privacy },
            )
            AgreeRow(
                text = "[선택] 마케팅 정보 수신 동의",
                checked = marketing,
                showView = true,
                onToggle = { marketing = !marketing },
            )
            Spacer(Modifier.height(12.dp))
        }
        Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
            DsButton(
                text = "동의하고 계속하기",
                enabled = requiredAll,
                onClick = { navigationHelper.navigateTo(ProfileSetupPage) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
            )
        }
    }
}

/** 상단 "카카오로 로그인됨" 배지(소셜 로그인 완료 표시). */
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
            text = "카카오로 로그인됨",
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
    showView: Boolean = false,
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
        if (showView) {
            DsText(
                text = "보기 ›",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
        }
    }
}
