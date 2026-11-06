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
            // 최초 접속에서 한 번만 받는다. 확인하면 다시 띄우지 않고, 이후 변경은 설정 > 알림이 맡는다.
            if (!consent.isDecided) dispatch(NotificationConsentReducerEvent.Shown)
        }

        override fun onIntent(intent: NotificationConsentIntent) {
            // 저장이 끝난 뒤에는 권한 획득 단계라 토글·확인을 받지 않는다.
            if (currentState.consentDone &&
                intent != NotificationConsentIntent.PermissionBlocked &&
                intent != NotificationConsentIntent.Finish
            ) {
                return
            }
            when (intent) {
                NotificationConsentIntent.ToggleService -> {
                    dispatch(NotificationConsentReducerEvent.ServiceToggled)
                }

                NotificationConsentIntent.ToggleMarketing -> {
                    dispatch(NotificationConsentReducerEvent.MarketingToggled)
                }

                NotificationConsentIntent.ToggleNight -> {
                    dispatch(NotificationConsentReducerEvent.NightToggled)
                }

                NotificationConsentIntent.Confirm -> {
                    confirm()
                }

                NotificationConsentIntent.PermissionBlocked -> {
                    dispatch(NotificationConsentReducerEvent.PermissionGuideShown)
                }

                NotificationConsentIntent.Finish -> {
                    dispatch(NotificationConsentReducerEvent.Finished)
                }
            }
        }

        private fun confirm() {
            val state = currentState
            if (!state.confirmEnabled) return
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

                NotificationConsentReducerEvent.ServiceToggled -> {
                    state.copy(serviceAgreed = !state.serviceAgreed)
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
                    if (state.marketingAgreed) {
                        state.copy(nightAgreed = !state.nightAgreed)
                    } else {
                        // 야간 수신은 광고성 동의가 선행돼야 한다(서버 규칙). 끄인 광고를 함께 켠다.
                        state.copy(marketingAgreed = true, nightAgreed = true)
                    }
                }

                NotificationConsentReducerEvent.SaveStarted -> {
                    state.copy(saving = true)
                }

                NotificationConsentReducerEvent.Confirmed -> {
                    // 시트는 닫지 않는다 — OS 권한 획득까지 마친 Finished 가 닫는다.
                    state.copy(saving = false, consentDone = true)
                }

                NotificationConsentReducerEvent.PermissionGuideShown -> {
                    state.copy(permissionGuideVisible = true)
                }

                NotificationConsentReducerEvent.Finished -> {
                    state.copy(visible = false, permissionGuideVisible = false)
                }
            }
    }
