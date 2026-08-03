package com.chillsam.courmy.main.presentation.login

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent

sealed interface LoginReducerEvent : ReducerEvent {
    data object Started : LoginReducerEvent

    data class Succeeded(
        val newUser: Boolean,
    ) : LoginReducerEvent

    data class Failed(
        val message: String,
    ) : LoginReducerEvent

    data object ResultConsumed : LoginReducerEvent

    data object ErrorConsumed : LoginReducerEvent
}
