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

        val onShowOneButtonDialog =
            remember<(MessageEffect.ShowOneButtonDialog) -> Unit> {
                { oneButtonDialogEffect = it }
            }
        MessageEffect(
            messageEffectFlow = messageHelper.effect,
            snackBarHostState = snackBarHostState,
            onShowOneButtonDialog = onShowOneButtonDialog,
        )

        oneButtonDialogEffect?.let { dialog ->
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

            StatusBarScrim(color = statusBarState.color ?: DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0)
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
