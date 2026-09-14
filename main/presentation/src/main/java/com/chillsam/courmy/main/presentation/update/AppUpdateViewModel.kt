package com.chillsam.courmy.main.presentation.update

import androidx.lifecycle.ViewModel
import com.chillsam.courmy.common.domain.appUpdate.AppUpdateEventBus
import com.chillsam.courmy.common.domain.appUpdate.AppUpdateRequired
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 강제 업데이트(HTTP 426) 신호를 화면 계층으로 전달한다.
 * [com.chillsam.courmy.main.presentation.navigation.RootComposable] 이 관찰해 앱 전체를 덮는다.
 *
 * MVI 를 두지 않는다 — 화면이 가진 상태가 "서버가 준 값 그대로" 하나뿐이라 Intent·ReducerEvent 를
 * 얹으면 통과만 하는 껍데기가 된다([com.chillsam.courmy.main.presentation.navigation.SessionExpiryViewModel] 과 같은 판단).
 */
@HiltViewModel
class AppUpdateViewModel
    @Inject
    constructor(
        appUpdateEventBus: AppUpdateEventBus,
    ) : ViewModel() {
        val updateRequired: StateFlow<AppUpdateRequired?> = appUpdateEventBus.updateRequired
    }
