package com.chillsam.courmy.common.data.appUpdate

import com.chillsam.courmy.common.domain.appUpdate.AppUpdateEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [AppUpdateEventBus] 기본 구현.
 *
 * 한 번 켜지면 끄지 않는다 — 426 은 스토어에서 앱을 갱신해야만 풀리므로 해제 API 를 두면 그걸 잘못
 * 부르는 경로가 생길 뿐이다. 화면이 뜨는 순간에도 다른 요청들이 같은 426 을 물고 들어오는데,
 * 값이 이미 true 라 재방출되지 않아 화면이 다시 그려지지도 않는다.
 */
@Singleton
class AppUpdateEventBusImpl
    @Inject
    constructor() : AppUpdateEventBus {
        private val _updateRequired = MutableStateFlow(false)
        override val updateRequired: StateFlow<Boolean> = _updateRequired.asStateFlow()

        override fun notifyUpdateRequired() {
            _updateRequired.value = true
        }
    }
