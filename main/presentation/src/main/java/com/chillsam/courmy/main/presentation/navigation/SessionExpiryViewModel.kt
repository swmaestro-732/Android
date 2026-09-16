package com.chillsam.courmy.main.presentation.navigation

import androidx.lifecycle.ViewModel
import com.chillsam.courmy.common.domain.session.SessionEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** 세션 만료(refresh 재발급 실패) 신호를 화면 계층으로 전달한다. [RootComposable] 이 관찰해 로그인으로 유도한다. */
@HiltViewModel
class SessionExpiryViewModel
    @Inject
    constructor(
        sessionEventBus: SessionEventBus,
    ) : ViewModel() {
        val expirations: Flow<Unit> = sessionEventBus.expirations
    }
