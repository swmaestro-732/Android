package com.chillsam.courmy.main.presentation.login

import com.chillsam.courmy.common.presentation.mvi.UiState

/** 소셜 로그인 결과 분기. */
enum class LoginResult {
    /** 기존 회원: 로그인 완료 → 홈으로. */
    EXISTING_USER,

    /** 신규 회원: 회원가입 플로우로. */
    NEW_USER,
}

/**
 * 로그인 화면 상태.
 * - [result] 는 성공 시 1회성 내비게이션 신호로, 소비 후 [onIntent] 로 비운다.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val result: LoginResult? = null,
) : UiState {
    companion object {
        val empty = LoginUiState()
    }
}
