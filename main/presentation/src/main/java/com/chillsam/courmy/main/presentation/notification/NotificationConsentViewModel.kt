package com.chillsam.courmy.main.presentation.notification

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.push.NotificationConsentStore
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationConsentViewModel
    @Inject
    constructor(
        private val consent: NotificationConsentStore,
    ) : MviViewModel<NotificationConsentIntent, NotificationConsentUIState, NotificationConsentReducerEvent>(
            NotificationConsentUIState(),
        ) {
        init {
            // 홈 최초 진입에서 한 번만 받는다. 확인 전에 앱을 껐다 켜면 다시 띄운다.
            if (!consent.isDecided) dispatch(NotificationConsentReducerEvent.Shown)
        }

        override fun onIntent(intent: NotificationConsentIntent) {
            when (intent) {
                NotificationConsentIntent.ToggleMarketing -> {
                    dispatch(NotificationConsentReducerEvent.MarketingToggled)
                }

                NotificationConsentIntent.ToggleNight -> {
                    dispatch(NotificationConsentReducerEvent.NightToggled)
                }

                NotificationConsentIntent.Confirm -> {
                    confirm()
                }
            }
        }

        private fun confirm() {
            val state = currentState
            if (state.saving) return
            dispatch(NotificationConsentReducerEvent.SaveStarted)
            viewModelScope.launch {
                consent.save(
                    marketingAgreed = state.marketingAgreed,
                    nightAgreed = state.nightAgreed,
                )
                dispatch(NotificationConsentReducerEvent.Confirmed)
            }
        }

        override fun reduce(
            state: NotificationConsentUIState,
            event: NotificationConsentReducerEvent,
        ): NotificationConsentUIState =
            when (event) {
                NotificationConsentReducerEvent.Shown -> {
                    state.copy(visible = true)
                }

                NotificationConsentReducerEvent.MarketingToggled -> {
                    val marketing = !state.marketingAgreed
                    // 광고성 동의를 철회하면 야간 동의도 함께 철회한다(서버 규칙과 동일).
                    state.copy(
                        marketingAgreed = marketing,
                        nightAgreed = state.nightAgreed && marketing,
                    )
                }

                NotificationConsentReducerEvent.NightToggled -> {
                    if (state.marketingAgreed) state.copy(nightAgreed = !state.nightAgreed) else state
                }

                NotificationConsentReducerEvent.SaveStarted -> {
                    state.copy(saving = true)
                }

                NotificationConsentReducerEvent.Confirmed -> {
                    state.copy(visible = false, saving = false)
                }
            }
    }
