package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.UiState

/** 아이디 중복 확인 결과. [message] 는 입력칸 아래에 그대로 노출된다. */
data class HandleCheckResult(
    val message: String,
    val available: Boolean,
)

/**
 * 프로필 편집(FS-26) 화면 상태.
 *
 * [originalNickname]·[originalHandle]·[originalImageUrl] 은 서버에서 불러온 편집 원본이고,
 * [nickname]·[handle]·[pickedImageUri] 는 사용자가 편집 중인 값이다. 저장 버튼 활성화는
 * "원본과 달라졌는가"([hasChanges])로 판정한다.
 */
data class ProfileEditUIState(
    val isLoading: Boolean = true,
    val loadErrorMessage: String? = null,
    val originalNickname: String = "",
    val originalHandle: String = "",
    val originalImageUrl: String = "",
    val nickname: String = "",
    val handle: String = "",
    val pickedImageUri: String? = null,
    val handleCheck: HandleCheckResult? = null,
    val isCheckingHandle: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    /** 저장이 끝나 화면을 닫아도 되는 상태. */
    val isSaved: Boolean = false,
) : UiState {
    val handleChanged: Boolean
        get() = handle != originalHandle

    /** 아이디를 바꿨다면 중복 확인에서 "사용 가능"을 받은 경우에만 저장할 수 있다. */
    val handleSaveable: Boolean
        get() = !handleChanged || handleCheck?.available == true

    val hasChanges: Boolean
        get() =
            handleSaveable &&
                (nickname != originalNickname || handleChanged || pickedImageUri != null)

    /** 아바타에 보여줄 이미지. 새로 고른 게 없으면 현재 프로필 사진. */
    val displayImageUrl: String
        get() = pickedImageUri ?: originalImageUrl

    companion object {
        val empty: ProfileEditUIState = ProfileEditUIState()
    }
}
