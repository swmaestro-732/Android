package com.chillsam.courmy.main.presentation.navigation

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.chillsam.courmy.common.domain.message.MessageEffect
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalMessageHelper
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.helper.LocalStatusBarState
import com.chillsam.courmy.common.presentation.helper.SessionUiState
import com.chillsam.courmy.common.presentation.helper.StatusBarState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.onboarding.SplashPage
import com.chillsam.courmy.main.presentation.update.AppUpdateRequiredScreen
import com.chillsam.courmy.main.presentation.update.AppUpdateViewModel
import kotlinx.coroutines.flow.Flow
import com.chillsam.courmy.main.domain.login.LoginPage as LoginRoute

@Composable
fun RootComposable(
    modifier: Modifier = Modifier,
    startStack: List<NavKey> = listOf(GenericNavKey(SplashPage.PATH)),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    var oneButtonDialogEffect by remember {
        mutableStateOf<MessageEffect.ShowOneButtonDialog?>(null)
    }

    DesignSystemTheme {
        val backStack = rememberNavBackStack(*startStack.toTypedArray())
        val messageHelper = LocalMessageHelper.current
        val navigationHelper = LocalNavigationHelper.current
        val sessionState = remember { SessionUiState() }
        val statusBarState = remember { StatusBarState() }

        // 세션 만료(refresh 재발급 실패) → 세션 해제 + 안내 후 로그인 화면으로 교체(백스택 초기화).
        val sessionExpiryViewModel: SessionExpiryViewModel = hiltViewModel()
        LaunchedEffect(Unit) {
            sessionExpiryViewModel.expirations.collect {
                sessionState.logout()
                messageHelper.showToast("세션이 만료되었어요. 다시 로그인해 주세요.")
                navigationHelper.navigateReplace(LoginRoute)
            }
        }

        val appUpdateViewModel: AppUpdateViewModel = hiltViewModel()
        val updateRequired by appUpdateViewModel.updateRequired.collectAsStateWithLifecycle()

        val onShowOneButtonDialog =
            remember<(MessageEffect.ShowOneButtonDialog) -> Unit> {
                { oneButtonDialogEffect = it }
            }
        MessageEffect(
            messageEffectFlow = messageHelper.effect,
            snackBarHostState = snackBarHostState,
            onShowOneButtonDialog = onShowOneButtonDialog,
        )

        // 강제 업데이트 중에는 띄우지 않는다. 이 다이얼로그도 별도 Window 라 안내 화면 위에 남는데,
        // 어차피 모든 요청이 426 이라 여기서 안내할 수 있는 건 "업데이트하라" 말고 없다.
        oneButtonDialogEffect?.takeUnless { updateRequired }?.let { dialog ->
            AlertDialog(
                onDismissRequest = {
                    if (!dialog.cantIgnore) oneButtonDialogEffect = null
                },
                title =
                    dialog.titleText?.let { titleText ->
                        {
                            DsText(
                                text = titleText,
                                style = DesignSystemThemeImpl.typeScale.textStrongM,
                                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1,
                                maxLines = Int.MAX_VALUE,
                            )
                        }
                    },
                text = {
                    DsText(
                        text = dialog.descText,
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
                        maxLines = Int.MAX_VALUE,
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dialog.onClickButton?.invoke()
                            oneButtonDialogEffect = null
                        },
                    ) {
                        DsText(
                            text = dialog.buttonText,
                            style = DesignSystemThemeImpl.typeScale.textStrongM,
                            color = DesignSystemThemeImpl.designSystemColor.contentAccent,
                        )
                    }
                },
                properties =
                    DialogProperties(
                        dismissOnBackPress = !dialog.cantIgnore,
                        dismissOnClickOutside = !dialog.cantIgnore,
                    ),
            )
        }

        Box(modifier = modifier.fillMaxSize()) {
            // 강제 업데이트(426)는 특정 화면의 에러가 아니라 앱 전체가 못 쓰게 된 상태다.
            // 위에 덮지 않고 화면 전체를 **갈아끼운다** — 덮는 방식은 다이얼로그를 이기지 못한다.
            // Compose 의 Dialog 는 자기 Window 를 만들어 Activity content view 위에 그려지므로,
            // 코스 상세에서 로그인 안내가 떠 있는 채로 426 이 오면 안내 화면이 그 뒤에 깔리고
            // 사용자는 다이얼로그의 "네"로 로그인 화면까지 빠져나갈 수 있다(실제로 재현됨).
            // AppNavHost 를 컴포지션에서 들어내면 그 안에서 열려 있던 다이얼로그도 함께 사라진다.
            // 스낵바 역시 Scaffold 와 같이 없어져 실패한 요청들의 안내가 올라오지 않는다.
            if (updateRequired) {
                AppUpdateRequiredScreen()
            } else {
                Scaffold(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1),
                    // 상·하단 시스템바 inset 은 소비하지 않는다(가로만 소비). 각 화면이 배경을 시스템바
                    // 뒤까지 그린 뒤 콘텐츠·하단 액션에만 status/navigationBarsPadding 을 적용한다.
                    contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
                    snackbarHost = { SnackbarHost(snackBarHostState) },
                ) { innerPadding ->
                    CompositionLocalProvider(
                        LocalSessionUiState provides sessionState,
                        LocalStatusBarState provides statusBarState,
                    ) {
                        AppNavHost(
                            backStack = backStack,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }

            // statusBarState 에는 직전 화면이 남긴 색이 그대로 있다. 코스 상세처럼 어두운 커버를 쓰던
            // 화면에서 넘어오면 흰 안내 화면 위에 어두운 띠가 얹히므로, 안내 화면 색으로 고정한다.
            StatusBarScrim(
                color =
                    when {
                        updateRequired -> DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1
                        else -> statusBarState.color ?: DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0
                    },
            )
        }
    }
}

/**
 * 상태바 영역을 칠하는 띠.
 *
 * targetSdk 35 부터 `window.statusBarColor` 가 무시되므로 색을 직접 그린다. 화면 위에 덮는
 * 방식이라, 이미 statusBarsPadding 으로 자리를 비워 둔 화면은 그 빈자리를 채우고 코스 상세처럼
 * 커버를 시스템바 뒤까지 그리는 화면은 그 위를 가린다.
 *
 * 색은 화면이 [StatusBarColor] 로 알려 준 값이고, 시스템 아이콘 명암은 그 색의 휘도로 정한다 —
 * 화면마다 어느 아이콘 색을 쓸지 따로 적어 두면 색을 바꿀 때 같이 고치는 걸 잊는다.
 */
@Composable
private fun BoxScope.StatusBarScrim(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val lightIcons = color.luminance() > LIGHT_SURFACE_LUMINANCE
        LaunchedEffect(lightIcons) {
            val window = (view.context as Activity).window
            // 밝은 배경 위에서만 아이콘을 어둡게 한다.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = lightIcons
        }
    }
    Spacer(
        modifier =
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(color),
    )
}

/** 이 휘도를 넘으면 "밝은 배경" 으로 보고 시스템 아이콘을 어둡게 한다. */
private const val LIGHT_SURFACE_LUMINANCE = 0.5f

@Composable
private fun MessageEffect(
    messageEffectFlow: Flow<MessageEffect>,
    snackBarHostState: SnackbarHostState,
    onShowOneButtonDialog: (MessageEffect.ShowOneButtonDialog) -> Unit,
) {
    val appContext = LocalContext.current.applicationContext

    LaunchedEffect(Unit) {
        messageEffectFlow.collect { effect ->
            when (effect) {
                is MessageEffect.ShowToastMsg -> {
                    Toast
                        .makeText(
                            appContext,
                            effect.message,
                            Toast.LENGTH_LONG,
                        ).show()
                }

                is MessageEffect.ShowSnackBarError -> {
                    snackBarHostState.showSnackbar(effect.message)
                }

                is MessageEffect.ShowOneButtonDialog -> {
                    onShowOneButtonDialog(effect)
                }
            }
        }
    }
}
