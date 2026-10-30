package com.chillsam.courmy.main.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.R
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.presentation.update.openStoreListing

@Composable
fun AppVersionPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val navigation = LocalNavigationHelper.current
    val viewModel: AppVersionViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val installedVersion =
        remember(context) {
            runCatching {
                val info = context.packageManager.getPackageInfo(context.packageName, 0)
                info.versionName.orEmpty()
            }.getOrDefault("버전 정보를 확인할 수 없어요")
        }
    RefreshOnResume { viewModel.onIntent(AppVersionIntent.Check) }
    AppVersionContent(
        state = state,
        installedVersion = installedVersion,
        onBack = { navigation.navigateToBack() },
        onCheck = { viewModel.onIntent(AppVersionIntent.Check) },
        onStore = { context.openStoreListing() },
        modifier = modifier,
    )
}

@Composable
private fun AppVersionContent(
    state: AppVersionUIState,
    installedVersion: String,
    onBack: () -> Unit,
    onCheck: () -> Unit,
    onStore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val type = DesignSystemThemeImpl.typeScale
    Column(modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(title = "앱 버전", onBack = onBack, boxed = true)
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DsText(text = "현재 버전", style = type.textRegularS, color = color.contentDefaultLevel2)
                DsText(text = installedVersion, style = type.textRegularS, color = color.contentDefaultLevel0)
            }
            state.latestVersion?.let { latestVersion ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DsText(text = "최신 버전", style = type.textRegularS, color = color.contentDefaultLevel2)
                    DsText(text = latestVersion, style = type.textRegularS, color = color.contentDefaultLevel0)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsText(
                    text =
                        when (state.status) {
                            AppVersionStatus.Loading -> "업데이트 확인 중…"
                            AppVersionStatus.Latest -> "최신 버전이에요."
                            AppVersionStatus.UpdateAvailable -> "새 버전이 있어요."
                            AppVersionStatus.Unavailable -> "버전을 확인하지 못했어요."
                        },
                    style = type.textRegularS,
                    color = color.contentDefaultLevel2,
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier.weight(1f),
                )
                if (state.status == AppVersionStatus.Unavailable) {
                    OutlinedIconButton(
                        onClick = onCheck,
                        border = BorderStroke(1.dp, color.borderDefaultLevel0),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh_24),
                            contentDescription = "버전 다시 확인",
                            tint = color.contentDefaultLevel2,
                        )
                    }
                }
            }
            if (state.status == AppVersionStatus.UpdateAvailable) {
                DsButton(
                    text = "업데이트하러 가기",
                    onClick = onStore,
                    variant = DsButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppVersionContentPreview() {
    DesignSystemTheme {
        AppVersionContent(AppVersionUIState(AppVersionStatus.UpdateAvailable), "1.0.0", {}, {}, {})
    }
}
