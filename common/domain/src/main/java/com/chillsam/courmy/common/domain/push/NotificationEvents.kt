package com.chillsam.courmy.common.domain.push

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** 수신 payload는 보관하지 않고 알림 목록을 서버에서 다시 읽어야 한다는 신호만 전달한다. */
@Singleton
class NotificationEvents
    @Inject
    constructor() {
        private val changed =
            MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        val changes = changed.asSharedFlow()

        fun notifyChanged() {
            changed.tryEmit(Unit)
        }
    }
