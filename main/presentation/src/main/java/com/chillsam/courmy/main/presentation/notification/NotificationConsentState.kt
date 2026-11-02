package com.chillsam.courmy.main.presentation.notification

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState

/**
 * 홈 최초 진입 알림 수신 동의 팝업 상태(SCRUM-725).
 *
 * 받는 동의는 광고성·야간 둘뿐이고 모두 선택이라 동의 없이도 계속할 수 있다.
 * 야간 동의는 광고성 동의가 선행돼야 한다(서버 `DeviceService` 와 같은 규칙).
 */
data class NotificationConsentUIState(
    val visible: Boolean = false,
    val marketingAgreed: Boolean = false,
    val nightAgreed: Boolean = false,
    val saving: Boolean = false,
) : UiState

sealed interface NotificationConsentIntent : MviIntent {
    data object ToggleMarketing : NotificationConsentIntent

    data object ToggleNight : NotificationConsentIntent

    data object Confirm : NotificationConsentIntent
}

sealed interface NotificationConsentReducerEvent : ReducerEvent {
    data object Shown : NotificationConsentReducerEvent

    data object MarketingToggled : NotificationConsentReducerEvent

    data object NightToggled : NotificationConsentReducerEvent

    data object SaveStarted : NotificationConsentReducerEvent

    data object Confirmed : NotificationConsentReducerEvent
}
