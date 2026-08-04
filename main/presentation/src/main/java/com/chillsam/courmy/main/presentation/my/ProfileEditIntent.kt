package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 프로필 편집(FS-26) 화면 입력. */
sealed interface ProfileEditIntent : MviIntent {
    /** 편집 원본이 될 현재 프로필을 불러온다(화면 진입 시 1회). */
    data object Load : ProfileEditIntent

    data object Retry : ProfileEditIntent

    data class NicknameChanged(
        val value: String,
    ) : ProfileEditIntent

    data class HandleChanged(
        val value: String,
    ) : ProfileEditIntent

    data class ImagePicked(
        val uri: String,
    ) : ProfileEditIntent

    /** 아이디 중복 확인 버튼. */
    data object CheckHandle : ProfileEditIntent

    data object Save : ProfileEditIntent

    /** 토스트로 소비한 에러 메시지를 비운다. */
    data object ConsumeError : ProfileEditIntent
}
