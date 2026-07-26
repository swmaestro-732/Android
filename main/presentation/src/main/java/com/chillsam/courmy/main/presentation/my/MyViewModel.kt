package com.chillsam.courmy.main.presentation.my

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 마이·프로필(FS-15) ViewModel.
 *
 * TODO-API-SPEC: 현재는 UI 확인용 더미 프로필([MyProfileUiState.sample])을 노출한다.
 * 프로필/내 코스 API 가 붙으면 여기서 관찰·변환한다.
 */
@HiltViewModel
class MyViewModel
    @Inject
    constructor() : ViewModel() {
        val profile: StateFlow<MyProfileUiState> = MutableStateFlow(MyProfileUiState.sample).asStateFlow()
    }
