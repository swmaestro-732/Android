package com.chillsam.courmy.main.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.translation.nativeName
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.component.BackTopBar

/**
 * 설정 > 언어. 표시 언어(한국어·영어·일본어)를 고른다.
 * 한국어가 아니면 코스 설명 등 한국어 본문에 "번역 보기"(온디바이스)가 나타난다.
 */
@Composable
fun LanguageSettingsPage(viewModel: LanguageSettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navigation = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor

    Column(Modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(title = "언어", onBack = navigation::navigateToBack, boxed = true)
        DsText(
            text = "한국어가 아니면 코스 설명 등에서 번역 보기를 사용할 수 있어요. 번역은 기기에서 처리돼요.",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
            maxLines = 2,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
        )
        AppLanguage.entries.forEach { language ->
            LanguageRow(
                label = language.nativeName(),
                selected = language == state.selected,
                onClick = { viewModel.onIntent(LanguageSettingsIntent.Select(language)) },
            )
            SettingsDivider()
        }
    }
}

@Composable
private fun LanguageRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = ScreenHorizontalPadding, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = if (selected) color.contentAccent else color.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_check_24),
                contentDescription = "선택됨",
                tint = color.contentAccent,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
