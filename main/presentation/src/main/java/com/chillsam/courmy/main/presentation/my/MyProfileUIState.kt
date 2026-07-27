package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.my.MyProfileVO

/** 마이·프로필(FS-15) 화면 상태. [profile] 이 있으면 정상, 없으면 [isLoading]/[errorMessage] 로 분기. */
data class MyProfileUIState(
    val isLoading: Boolean = true,
    val profile: MyProfileVO? = null,
    val errorMessage: String? = null,
) : UiState {
    companion object {
        val empty: MyProfileUIState = MyProfileUIState()
    }
}
