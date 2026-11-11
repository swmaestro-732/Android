package com.chillsam.courmy.main.presentation.settings

import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.presentation.mvi.UiState

/** 언어 설정 화면 상태. [selected] 가 현재 저장된 표시 언어다(기본 한국어). */
data class LanguageSettingsUiState(
    val selected: AppLanguage = AppLanguage.KOREAN,
) : UiState
