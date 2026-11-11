package com.chillsam.courmy.main.presentation.settings

import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface LanguageSettingsIntent : MviIntent {
    /** 표시 언어를 고른다. */
    data class Select(
        val language: AppLanguage,
    ) : LanguageSettingsIntent
}
