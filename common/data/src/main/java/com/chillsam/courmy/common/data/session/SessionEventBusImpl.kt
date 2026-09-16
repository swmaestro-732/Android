package com.chillsam.courmy.common.data.session

import com.chillsam.courmy.common.domain.session.SessionEventBus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [SessionEventBus] 기본 구현. replay=0(과거 만료를 새 관찰자에게 재전달하지 않음) +
 * extraBufferCapacity=1 로 관찰자가 없어도 [notifyExpired] 가 suspend 없이 방출된다.
 */
@Singleton
class SessionEventBusImpl
    @Inject
    constructor() : SessionEventBus {
        private val _expirations = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
        override val expirations: Flow<Unit> = _expirations.asSharedFlow()

        override fun notifyExpired() {
            _expirations.tryEmit(Unit)
        }
    }
