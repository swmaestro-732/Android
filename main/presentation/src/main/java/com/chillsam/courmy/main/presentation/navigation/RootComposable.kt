package com.chillsam.courmy.main.presentation.navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.chillsam.courmy.common.domain.message.MessageEffect
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalMessageHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.HomePage
import kotlinx.coroutines.flow.Flow

@Composable
fun RootComposable(
    modifier: Modifier = Modifier,
    startStack: List<NavKey> = listOf(GenericNavKey(HomePage.PATH)),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    var oneButtonDialogEffect by remember {
        mutableStateOf<MessageEffect.ShowOneButtonDialog?>(null)
    }

    DesignSystemTheme {
        val backStack = rememberNavBackStack(*startStack.toTypedArray())
        val messageHelper = LocalMessageHelper.current

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

        Scaffold(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1),
            // 상·하단 시스템바 inset 은 소비하지 않는다(가로만 소비). 각 화면이 배경을 시스템바
            // 뒤까지 그린 뒤 콘텐츠·하단 액션에만 status/navigationBarsPadding 을 적용해,
            // 상태바·내비게이션바 영역 색을 화면 배경과 일치시킨다.
            contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
            snackbarHost = { SnackbarHost(snackBarHostState) },
        ) { innerPadding ->
            AppNavHost(
                backStack = backStack,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

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
