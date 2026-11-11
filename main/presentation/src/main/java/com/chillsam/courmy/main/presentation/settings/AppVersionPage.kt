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
import androidx.compose.ui.res.stringResource
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
import com.chillsam.courmy.main.presentation.R as MainR

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
            }.getOrDefault(context.getString(MainR.string.main_app_version_unknown))
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
        BackTopBar(title = stringResource(MainR.string.main_app_version_title), onBack = onBack, boxed = true)
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DsText(
                    text = stringResource(MainR.string.main_app_version_current_label),
                    style = type.textRegularS,
                    color = color.contentDefaultLevel2,
                )
                DsText(text = installedVersion, style = type.textRegularS, color = color.contentDefaultLevel0)
            }
            state.latestVersion?.let { latestVersion ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DsText(
                        text = stringResource(MainR.string.main_app_version_latest_label),
                        style = type.textRegularS,
                        color = color.contentDefaultLevel2,
                    )
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
                            AppVersionStatus.Loading -> {
                                stringResource(MainR.string.main_app_version_status_loading)
                            }

                            AppVersionStatus.Latest -> {
                                stringResource(MainR.string.main_app_version_status_latest)
                            }

                            AppVersionStatus.UpdateAvailable -> {
                                stringResource(
                                    MainR.string.main_app_version_status_update_available,
                                )
                            }

                            AppVersionStatus.Unavailable -> {
                                stringResource(
                                    MainR.string.main_app_version_status_unavailable,
                                )
                            }
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
                            contentDescription = stringResource(MainR.string.main_app_version_recheck),
                            tint = color.contentDefaultLevel2,
                        )
                    }
                }
            }
            if (state.status == AppVersionStatus.UpdateAvailable) {
                DsButton(
                    text = stringResource(MainR.string.main_app_update_required_cta),
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
