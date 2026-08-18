package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO

sealed interface MyProfileReducerEvent : ReducerEvent {
    data object LoadStarted : MyProfileReducerEvent

    data class Loaded(
        val profile: MyProfileVO,
    ) : MyProfileReducerEvent

    data class Failed(
        val message: String,
    ) : MyProfileReducerEvent

    data object LoadMoreStarted : MyProfileReducerEvent

    data class MoreLoaded(
        val courses: List<ProfileCourseVO>,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : MyProfileReducerEvent

    /** 이어받기 실패는 화면을 되돌리지 않는다 — 이미 보고 있는 목록은 그대로 두고 안내만 한다. */
    data object MoreFailed : MyProfileReducerEvent
}
