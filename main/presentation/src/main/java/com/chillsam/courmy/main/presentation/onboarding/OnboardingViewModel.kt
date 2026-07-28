package com.chillsam.courmy.main.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.main.domain.onboarding.CompleteOnboardingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 온보딩 완료 처리. 플래그를 저장한 뒤 [onDone](네비게이션)을 실행한다. */
@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    ) : ViewModel() {
        fun complete(onDone: () -> Unit) {
            viewModelScope.launch {
                completeOnboardingUseCase()
                onDone()
            }
        }
    }
