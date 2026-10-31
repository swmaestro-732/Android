package com.chillsam.courmy.main.presentation.settings

import androidx.lifecycle.viewModelScope
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
    val saving: Boolean = false,
    val error: Boolean = false,
) : UiState

sealed interface NotificationSettingsIntent : MviIntent {
    data object Refresh : NotificationSettingsIntent

    data object DismissError : NotificationSettingsIntent

    data class SetEnabled(
        val enabled: Boolean,
    ) : NotificationSettingsIntent
}

sealed interface NotificationSettingsEvent : ReducerEvent {
    data object Saving : NotificationSettingsEvent

    data class Loaded(
        val enabled: Boolean,
        val error: Boolean = false,
    ) : NotificationSettingsEvent
}

@HiltViewModel
class NotificationSettingsViewModel
    @Inject
    constructor(
        private val settings: NotificationSettings,
    ) : MviViewModel<NotificationSettingsIntent, NotificationSettingsUIState, NotificationSettingsEvent>(
            NotificationSettingsUIState(enabled = settings.notificationsEnabled),
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
                                dispatch(NotificationSettingsEvent.Loaded(settings.notificationsEnabled, error = true))
                            }
                    }
                }
            }
        }

        private fun refresh() = dispatch(NotificationSettingsEvent.Loaded(settings.notificationsEnabled))

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
                        error = event.error,
                    )
                }
            }
    }
