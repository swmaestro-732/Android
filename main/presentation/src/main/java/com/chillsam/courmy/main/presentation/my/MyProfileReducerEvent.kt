package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.my.MyProfileVO

sealed interface MyProfileReducerEvent : ReducerEvent {
    data object LoadStarted : MyProfileReducerEvent

    data class Loaded(
        val profile: MyProfileVO,
    ) : MyProfileReducerEvent

    data class Failed(
        val message: String,
    ) : MyProfileReducerEvent
}
