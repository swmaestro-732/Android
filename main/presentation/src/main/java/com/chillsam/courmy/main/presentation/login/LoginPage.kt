package com.chillsam.courmy.main.presentation.login

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.login.ProfileSetupPage

private val KakaoYellow = Color(0xFFFEE500)

/** 네이버 브랜드 고정색. 디자인 토큰이 아니라 브랜드 가이드 값이라 raw hex 를 쓴다(카카오와 동일). */
private val NaverGreen = Color(0xFF03C75A)

/**
 * 로그인·시작하기 화면(FS-03). "카카오로 시작하기" → 카카오 SDK 로그인(idToken) → 서버 social-login.
 * 기존 회원이면 홈으로, 신규 회원이면 회원가입 플로우(프로필→…→완료)로 진입한다.
 */
@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    // 상단이 흰색이라 상태바도 같은 색으로 이어 붙인다. 회색으로 두면 띠만 분리돼 보인다.
    StatusBarColor(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val color = DesignSystemThemeImpl.designSystemColor
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val canStartLogin = !uiState.isLoading && !uiState.isSelectingGoogleAccount

    BackHandler(enabled = uiState.isSelectingGoogleAccount) {
        viewModel.onIntent(LoginIntent.CancelGoogleLogin)
    }

    // 로그인 성공 → 분기 내비게이션(1회성). 기존 회원은 세션 로그인 후 홈, 신규 회원은 가입 플로우.
    LaunchedEffect(uiState.result) {
        when (uiState.result) {
            LoginResult.EXISTING_USER -> {
                session.login()
                navigationHelper.navigateReplace(HomePage)
            }

            LoginResult.NEW_USER -> {
                navigationHelper.navigateTo(ProfileSetupPage)
            }

            null -> {
                Unit
            }
        }
        if (uiState.result != null) viewModel.onIntent(LoginIntent.ConsumeResult)
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(LoginIntent.ConsumeError)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel1)) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding),
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
                if (uiState.isSelectingGoogleAccount) {
                    DsText(
                        text = "Google 계정을 선택해 주세요.\n선택창이 열리지 않으면 취소 후 다시 시도해 주세요.",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel1,
                    )
                    SocialButton(
                        text = "Google 로그인 취소",
                        iconRes = null,
                        background = color.bgDefaultLevel0,
                        textColor = color.contentDefaultLevel0,
                        onClick = { viewModel.onIntent(LoginIntent.CancelGoogleLogin) },
                    )
                }
                SocialButton(
                    text = "카카오로 시작하기",
                    iconRes = R.drawable.ic_kakao_24,
                    background = KakaoYellow,
                    textColor = color.contentDefaultLevel0,
                    enabled = canStartLogin,
                    onClick = { viewModel.onIntent(LoginIntent.KakaoLogin(context)) },
                )
                SocialButton(
                    text = "네이버로 시작하기",
                    iconRes = R.drawable.ic_naver_24,
                    background = NaverGreen,
                    textColor = color.contentOnAccent,
                    enabled = canStartLogin,
                    onClick = { viewModel.onIntent(LoginIntent.NaverLogin(context)) },
                )
                SocialButton(
                    text = "Google로 시작하기",
                    iconRes = R.drawable.ic_google_24,
                    background = color.bgDefaultLevel1,
                    textColor = color.contentDefaultLevel0,
                    border = color.borderDefaultLevel0,
                    enabled = canStartLogin,
                    onClick = { viewModel.onIntent(LoginIntent.GoogleLogin(context)) },
                )
                SocialButton(
                    text = "게스트로 돌아가기",
                    iconRes = null,
                    background = color.bgDefaultLevel0,
                    textColor = color.contentDefaultLevel1,
                    enabled = !uiState.isLoading,
                    onClick = {
                        viewModel.onIntent(LoginIntent.CancelGoogleLogin)
                        navigationHelper.navigateToBack()
                    },
                )
            }
            DsText(
                text = "처음이라면 프로필 설정이 이어져요.",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
            Spacer(Modifier.navigationBarsPadding().height(12.dp))
        }

        if (uiState.isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(color.contentDefaultLevel0.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = color.contentAccent)
            }
        }
    }
}

@Composable
private fun SocialButton(
    text: String,
    @DrawableRes iconRes: Int?,
    background: Color,
    textColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
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
                .clickable(enabled = enabled, onClick = onClick),
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
