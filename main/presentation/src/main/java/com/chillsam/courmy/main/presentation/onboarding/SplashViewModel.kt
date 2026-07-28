package com.chillsam.courmy.main.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.main.domain.onboarding.GetOnboardedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 스플래시에서 온보딩 완료 여부를 로드한다(로드 전엔 null). */
@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val getOnboardedUseCase: GetOnboardedUseCase,
    ) : ViewModel() {
        private val _onboarded = MutableStateFlow<Boolean?>(null)
        val onboarded: StateFlow<Boolean?> = _onboarded.asStateFlow()

        init {
            viewModelScope.launch {
                // DataStore 읽기 실패해도 스플래시가 멈추지 않도록 항상 값을 채운다(실패 시 온보딩부터).
                _onboarded.value = runCatching { getOnboardedUseCase() }.getOrDefault(false)
            }
        }
    }
