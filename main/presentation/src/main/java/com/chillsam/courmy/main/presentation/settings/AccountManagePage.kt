package com.chillsam.courmy.main.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.presentation.R as MainR

/** 계정 관리 화면. 설정 목록의 stringResource(MainR.string.main_account_manage_title) 상세로, 로그아웃·회원 탈퇴를 담당한다. */
@Composable
fun AccountManagePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val color = DesignSystemThemeImpl.designSystemColor
    val viewModel: AccountViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showWithdrawDialog by remember { mutableStateOf(false) }

    // 로그아웃/탈퇴 완료 → 세션 해제 후 게스트 홈으로.
    LaunchedEffect(state.result) {
        if (state.result != null) {
            session.logout()
            navigationHelper.navigateReplace(HomePage)
            viewModel.onIntent(AccountIntent.ConsumeResult)
        }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(AccountIntent.ConsumeError)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(modifier = Modifier.fillMaxSize()) {
            BackTopBar(title = stringResource(MainR.string.main_account_manage_title), onBack = {
                navigationHelper.navigateToBack()
            }, boxed = true)

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
            ) {
                SettingsDangerRow(label = stringResource(MainR.string.main_account_manage_logout), onClick = {
                    viewModel.onIntent(AccountIntent.Logout)
                })
                SettingsDivider()
                SettingsDangerRow(label = stringResource(MainR.string.main_account_manage_withdraw), onClick = {
                    showWithdrawDialog =
                        true
                })
            }
        }

        if (state.isLoading) {
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

    if (showWithdrawDialog) {
        DsConfirmDialog(
            title = stringResource(MainR.string.main_account_manage_withdraw_dialog_title),
            description = stringResource(MainR.string.main_account_manage_withdraw_dialog_description),
            destructive = true,
            onConfirm = {
                showWithdrawDialog = false
                viewModel.onIntent(AccountIntent.Withdraw)
            },
            onDismiss = { showWithdrawDialog = false },
        )
    }
}
