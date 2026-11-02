package com.chillsam.courmy.main.presentation.notification

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState

/**
 * 홈 최초 진입 알림 수신 동의 팝업 상태(SCRUM-725).
 *
 * 서비스 알림(필수)에 동의해야 확인할 수 있고, 야간 동의는 광고성 동의가 선행돼야 한다
 * (서버 `DeviceService` 와 같은 규칙).
 */
data class NotificationConsentUIState(
    val visible: Boolean = false,
    val serviceAgreed: Boolean = false,
    val marketingAgreed: Boolean = false,
    val nightAgreed: Boolean = false,
    val saving: Boolean = false,
) : UiState {
    val confirmEnabled: Boolean get() = serviceAgreed && !saving
}

sealed interface NotificationConsentIntent : MviIntent {
    data object ToggleService : NotificationConsentIntent

    data object ToggleMarketing : NotificationConsentIntent

    data object ToggleNight : NotificationConsentIntent

    data object Confirm : NotificationConsentIntent
}

sealed interface NotificationConsentReducerEvent : ReducerEvent {
    data object Shown : NotificationConsentReducerEvent

    data object ServiceToggled : NotificationConsentReducerEvent

    data object MarketingToggled : NotificationConsentReducerEvent

    data object NightToggled : NotificationConsentReducerEvent

    data object SaveStarted : NotificationConsentReducerEvent

    data object Confirmed : NotificationConsentReducerEvent
}
