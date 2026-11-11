package com.chillsam.courmy.main.presentation.settings

import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent

sealed interface LanguageSettingsReducerEvent : ReducerEvent {
    /** 저장소에서 읽은(또는 방금 바꾼) 선택 언어를 반영한다. */
    data class Selected(
        val language: AppLanguage,
    ) : LanguageSettingsReducerEvent
}
