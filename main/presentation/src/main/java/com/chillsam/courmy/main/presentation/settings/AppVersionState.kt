package com.chillsam.courmy.main.presentation.settings

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState

enum class AppVersionStatus {
    Loading,
    Latest,
    UpdateAvailable,
    Unavailable,
}

data class AppVersionUIState(
    val status: AppVersionStatus = AppVersionStatus.Loading,
    val latestVersion: String? = null,
) : UiState

sealed interface AppVersionIntent : MviIntent {
    data object Check : AppVersionIntent
}

sealed interface AppVersionReducerEvent : ReducerEvent {
    data object CheckStarted : AppVersionReducerEvent

    data class Checked(
        val status: AppVersionStatus,
        val latestVersion: String?,
    ) : AppVersionReducerEvent
}
