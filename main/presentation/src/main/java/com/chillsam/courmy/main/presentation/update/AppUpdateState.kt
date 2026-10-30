package com.chillsam.courmy.main.presentation.update

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState

sealed interface AppUpdateIntent : MviIntent {
    data object CheckOptionalUpdate : AppUpdateIntent

    data object DismissOptionalUpdate : AppUpdateIntent

    data object AcceptOptionalUpdate : AppUpdateIntent
}

data class AppUpdateUIState(
    val checked: Boolean = false,
    val showOptionalUpdate: Boolean = false,
) : UiState

sealed interface AppUpdateReducerEvent : ReducerEvent {
    data object CheckStarted : AppUpdateReducerEvent

    data class Checked(
        val available: Boolean,
    ) : AppUpdateReducerEvent

    data object Dismissed : AppUpdateReducerEvent
}
