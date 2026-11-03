package com.chillsam.courmy.main.presentation.notification

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState

/**
 * 홈 최초 진입 알림 수신 동의 팝업 상태(SCRUM-725).
 *
 * 활동 알림은 필수라 꺼진 상태로는 확인할 수 없다. 광고성·야간은 선택이고,
 * 야간 동의는 광고성 동의가 선행돼야 한다(서버 `DeviceService` 와 같은 규칙).
 */
data class NotificationConsentUIState(
    val visible: Boolean = false,
    // 필수 항목이라 켜진 채로 시작한다. 끄면 확인 버튼이 비활성화된다.
    val serviceAgreed: Boolean = true,
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
