package com.chillsam.courmy.main.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.main.domain.auth.RestoreSessionUseCase
import com.chillsam.courmy.main.domain.onboarding.GetOnboardedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 스플래시 진입 목적지. 저장된 세션 복원(자동 로그인)과 온보딩 완료 여부를 함께 싣는다. */
data class SplashDestination(
    val onboarded: Boolean,
    val loggedIn: Boolean,
)

/**
 * 스플래시에서 저장된 세션을 복원(자동 로그인)하고 온보딩 완료 여부를 로드한다(로드 전엔 null).
 * 세션 복원/온보딩 로드가 실패해도 스플래시가 멈추지 않도록 각각 기본값(false)으로 흘려보낸다.
 */
@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val getOnboardedUseCase: GetOnboardedUseCase,
        private val restoreSessionUseCase: RestoreSessionUseCase,
    ) : ViewModel() {
        private val _destination = MutableStateFlow<SplashDestination?>(null)
        val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

        init {
            viewModelScope.launch {
                val loggedIn = runCatching { restoreSessionUseCase() }.getOrDefault(false)
                val onboarded = runCatching { getOnboardedUseCase() }.getOrDefault(false)
                _destination.value = SplashDestination(onboarded = onboarded, loggedIn = loggedIn)
            }
        }
    }
