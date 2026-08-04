package com.chillsam.courmy.main.presentation.user

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.user.UserProfileVO

/**
 * 타유저 프로필(FS-15) 화면 상태.
 * [profile] 이 있으면 정상, 없으면 [isLoading]/[errorMessage] 로 분기한다.
 * [followErrorMessage] 는 노출 후 [UserProfileIntent.ConsumeFollowError] 로 지우는 1회성 안내다.
 */
data class UserProfileUIState(
    val isLoading: Boolean = true,
    val profile: UserProfileVO? = null,
    val errorMessage: String? = null,
    val isFollowInFlight: Boolean = false,
    val followErrorMessage: String? = null,
) : UiState {
    companion object {
        val empty: UserProfileUIState = UserProfileUIState()
    }
}
