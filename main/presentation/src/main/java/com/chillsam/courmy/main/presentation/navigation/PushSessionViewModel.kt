package com.chillsam.courmy.main.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.auth.SessionRepository
import com.chillsam.courmy.common.domain.push.NotificationEvents
import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.main.domain.auth.RestoreSessionUseCase
import com.chillsam.courmy.main.domain.notification.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 복원된 백스택이 Splash를 건너뛰어도 푸시는 실제 세션 복원을 기다린다. */
@HiltViewModel
class PushSessionViewModel
    @Inject
    constructor(
        private val session: SessionRepository,
        private val notifications: NotificationRepository,
        private val notificationEvents: NotificationEvents,
        private val telemetry: Telemetry,
        restoreSession: RestoreSessionUseCase,
    ) : ViewModel() {
        private val ready = MutableStateFlow(false)
        val sessionReady = ready.asStateFlow()
        val isLoggedIn: Boolean get() = session.isLoggedIn

        fun markRead(id: Long) {
            viewModelScope.launch {
                runCatching { notifications.markRead(id) }
                    .onSuccess { notificationEvents.notifyChanged() }
                    .onFailure {
                        if (it is CancellationException) throw it
                        telemetry.recordFailure(AppFailure("notification", "mark_read_failed"))
                    }
            }
        }

        init {
            viewModelScope.launch {
                restoreSession()
                ready.value = true
            }
        }
    }
