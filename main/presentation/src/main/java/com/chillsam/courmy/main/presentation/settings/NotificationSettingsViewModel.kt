package com.chillsam.courmy.main.presentation.settings

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.push.NotificationConsentStore
import com.chillsam.courmy.common.domain.push.NotificationSettings
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationSettingsUIState(
    val enabled: Boolean = false,
    val marketing: Boolean = false,
    val night: Boolean = false,
    val saving: Boolean = false,
    val error: Boolean = false,
) : UiState

sealed interface NotificationSettingsIntent : MviIntent {
    data object Refresh : NotificationSettingsIntent

    data object DismissError : NotificationSettingsIntent

    data class SetEnabled(
        val enabled: Boolean,
    ) : NotificationSettingsIntent

    data class SetMarketing(
        val enabled: Boolean,
    ) : NotificationSettingsIntent

    data class SetNight(
        val enabled: Boolean,
    ) : NotificationSettingsIntent
}

sealed interface NotificationSettingsEvent : ReducerEvent {
    data object Saving : NotificationSettingsEvent

    data class Loaded(
        val enabled: Boolean,
        val marketing: Boolean,
        val night: Boolean,
        val error: Boolean = false,
    ) : NotificationSettingsEvent
}

@HiltViewModel
class NotificationSettingsViewModel
    @Inject
    constructor(
        private val settings: NotificationSettings,
        private val consent: NotificationConsentStore,
    ) : MviViewModel<NotificationSettingsIntent, NotificationSettingsUIState, NotificationSettingsEvent>(
            NotificationSettingsUIState(
                enabled = settings.notificationsEnabled,
                marketing = consent.marketingAgreed,
                night = consent.nightAgreed,
            ),
        ) {
        override fun onIntent(intent: NotificationSettingsIntent) {
            when (intent) {
                NotificationSettingsIntent.Refresh -> {
                    if (!currentState.saving) refresh()
                }

                NotificationSettingsIntent.DismissError -> {
                    refresh()
                }

                is NotificationSettingsIntent.SetEnabled -> {
                    if (currentState.saving) return
                    dispatch(NotificationSettingsEvent.Saving)
                    viewModelScope.launch {
                        runCatching { settings.setNotificationsEnabled(intent.enabled) }
                            .onSuccess { refresh() }
                            .onFailure { failure ->
                                if (failure is CancellationException && failure !is TimeoutCancellationException) {
                                    throw failure
                                }
                                if (failure is Error) throw failure
                                dispatch(loaded(error = true))
                            }
                    }
                }

                is NotificationSettingsIntent.SetMarketing -> {
                    // 광고 철회는 야간도 함께 철회한다(서버 규칙과 동일). 저장은 로컬 즉시,
                    // 서버 반영은 store 가 백그라운드에서 재시도까지 맡는다.
                    saveConsent(
                        marketing = intent.enabled,
                        night = consent.nightAgreed && intent.enabled,
                    )
                }

                is NotificationSettingsIntent.SetNight -> {
                    if (!consent.marketingAgreed) return
                    saveConsent(marketing = true, night = intent.enabled)
                }
            }
        }

        private fun saveConsent(
            marketing: Boolean,
            night: Boolean,
        ) {
            if (currentState.saving) return
            dispatch(NotificationSettingsEvent.Saving)
            viewModelScope.launch {
                runCatching { consent.save(marketingAgreed = marketing, nightAgreed = night) }
                    .onSuccess { refresh() }
                    .onFailure { failure ->
                        if (failure is CancellationException && failure !is TimeoutCancellationException) {
                            throw failure
                        }
                        if (failure is Error) throw failure
                        dispatch(loaded(error = true))
                    }
            }
        }

        private fun refresh() = dispatch(loaded())

        private fun loaded(error: Boolean = false) =
            NotificationSettingsEvent.Loaded(
                enabled = settings.notificationsEnabled,
                marketing = consent.marketingAgreed,
                night = consent.nightAgreed,
                error = error,
            )

        override fun reduce(
            state: NotificationSettingsUIState,
            event: NotificationSettingsEvent,
        ): NotificationSettingsUIState =
            when (event) {
                NotificationSettingsEvent.Saving -> {
                    state.copy(saving = true, error = false)
                }

                is NotificationSettingsEvent.Loaded -> {
                    NotificationSettingsUIState(
                        enabled = event.enabled,
                        marketing = event.marketing,
                        night = event.night,
                        error = event.error,
                    )
                }
            }
    }
