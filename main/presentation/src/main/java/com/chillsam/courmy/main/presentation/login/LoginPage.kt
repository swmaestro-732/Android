package com.chillsam.courmy.main.presentation.login

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.login.TermsAgreementPage

private val KakaoYellow = Color(0xFFFEE500)

/**
 * 로그인·시작하기 화면(FS-03). 카카오/구글 소셜 로그인으로 회원가입 플로우(약관→프로필→…→완료)에 진입,
 * "게스트로 돌아가기"는 로그인 없이 이전 화면(게스트)으로 복귀한다.
 */
@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel1)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(40.dp))
        DsText(
            text = "Courmy와\n함께 시작해요",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = color.contentDefaultLevel0,
            maxLines = 2,
        )
        DsText(
            text = "계정으로 3초 만에 자동 연결돼요.\n처음이면 바로 가입돼요.",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
            maxLines = 2,
            modifier = Modifier.padding(top = 12.dp),
        )

        Spacer(Modifier.weight(1f))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SocialButton(
                text = "카카오로 시작하기",
                iconRes = R.drawable.ic_kakao_24,
                background = KakaoYellow,
                textColor = color.contentDefaultLevel0,
                onClick = { navigationHelper.navigateTo(TermsAgreementPage) },
            )
            SocialButton(
                text = "구글로 시작하기",
                iconRes = R.drawable.ic_google_24,
                background = color.bgDefaultLevel1,
                textColor = color.contentDefaultLevel0,
                border = color.borderDefaultLevel1,
                onClick = { navigationHelper.navigateTo(TermsAgreementPage) },
            )
            SocialButton(
                text = "게스트로 돌아가기",
                iconRes = null,
                background = color.bgDefaultLevel0,
                textColor = color.contentDefaultLevel1,
                onClick = { navigationHelper.navigateToBack() },
            )
        }
        DsText(
            text = "로그인 후 약관 동의 절차가 이어져요.",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel3,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
        Spacer(Modifier.navigationBarsPadding().height(12.dp))
    }
}

@Composable
private fun SocialButton(
    text: String,
    @DrawableRes iconRes: Int?,
    background: Color,
    textColor: Color,
    onClick: () -> Unit,
    border: Color? = null,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (border != null) {
                        Modifier.border(1.dp, border, RoundedCornerShape(12.dp))
                    } else {
                        Modifier
                    },
                ).background(background)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp),
                )
            }
            DsText(
                text = text,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = textColor,
            )
        }
    }
}
