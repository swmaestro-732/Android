package com.chillsam.courmy.common.data.appUpdate

import com.chillsam.courmy.common.domain.appUpdate.AppUpdateEventBus
import com.chillsam.courmy.common.domain.appUpdate.AppUpdateRequired
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [AppUpdateEventBus] 기본 구현.
 *
 * 한 번 켜지면 끄지 않는다 — 426 은 스토어에서 앱을 갱신해야만 풀리므로 해제 API 를 두면 그걸 잘못
 * 부르는 경로가 생길 뿐이다. 첫 신호의 문구를 유지하는 이유도 같다. 화면이 뜨는 순간에도 다른 요청들이
 * 같은 426 을 물고 들어오는데, 매번 덮어쓰면 사용자가 읽는 도중에 문구가 바뀐다.
 */
@Singleton
class AppUpdateEventBusImpl
    @Inject
    constructor() : AppUpdateEventBus {
        private val _updateRequired = MutableStateFlow<AppUpdateRequired?>(null)
        override val updateRequired: StateFlow<AppUpdateRequired?> = _updateRequired.asStateFlow()

        override fun notifyUpdateRequired(message: String?) {
            _updateRequired.compareAndSet(
                expect = null,
                update = AppUpdateRequired(message = message?.takeIf { it.isNotBlank() }),
            )
        }
    }
