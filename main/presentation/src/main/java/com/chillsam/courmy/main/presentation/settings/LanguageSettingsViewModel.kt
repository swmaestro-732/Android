package com.chillsam.courmy.main.presentation.settings

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.translation.LanguageRepository
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 언어 설정 ViewModel. 저장된 표시 언어를 구독해 노출하고, 선택을 저장소에 반영한다.
 * 저장 후 상태 변경은 구독 플로우가 다시 흘려보내므로 한 곳([reduce])에서만 변이한다.
 */
@HiltViewModel
class LanguageSettingsViewModel
    @Inject
    constructor(
        private val languageRepository: LanguageRepository,
    ) : MviViewModel<LanguageSettingsIntent, LanguageSettingsUiState, LanguageSettingsReducerEvent>(
            LanguageSettingsUiState(),
        ) {
        init {
            viewModelScope.launch {
                languageRepository.language.collect { language ->
                    dispatch(LanguageSettingsReducerEvent.Selected(language))
                }
            }
        }

        override fun onIntent(intent: LanguageSettingsIntent) {
            when (intent) {
                is LanguageSettingsIntent.Select -> {
                    viewModelScope.launch { languageRepository.setLanguage(intent.language) }
                }
            }
        }

        override fun reduce(
            state: LanguageSettingsUiState,
            event: LanguageSettingsReducerEvent,
        ): LanguageSettingsUiState =
            when (event) {
                is LanguageSettingsReducerEvent.Selected -> state.copy(selected = event.language)
            }
    }
