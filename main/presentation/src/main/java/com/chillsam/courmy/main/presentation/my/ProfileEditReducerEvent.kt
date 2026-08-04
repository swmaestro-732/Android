package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.my.MyProfileVO

/** 프로필 편집 화면의 내부 상태 변경 이벤트. */
sealed interface ProfileEditReducerEvent : ReducerEvent {
    data object LoadStarted : ProfileEditReducerEvent

    /** 편집 원본 로드 완료. 입력 필드의 초기값도 이 값으로 채운다. */
    data class Loaded(
        val profile: MyProfileVO,
    ) : ProfileEditReducerEvent

    data class LoadFailed(
        val message: String,
    ) : ProfileEditReducerEvent

    data class NicknameChanged(
        val value: String,
    ) : ProfileEditReducerEvent

    data class HandleChanged(
        val value: String,
    ) : ProfileEditReducerEvent

    data class ImagePicked(
        val uri: String,
    ) : ProfileEditReducerEvent

    data object HandleCheckStarted : ProfileEditReducerEvent

    /**
     * [handle] 은 실제로 확인한 값이다. 확인 중에 사용자가 값을 더 고쳤을 수 있으므로,
     * reducer 가 현재 입력과 대조해 어긋나면 결과를 버린다.
     */
    data class HandleChecked(
        val handle: String,
        val result: HandleCheckResult,
    ) : ProfileEditReducerEvent

    data object SaveStarted : ProfileEditReducerEvent

    data object Saved : ProfileEditReducerEvent

    data class SaveFailed(
        val message: String,
    ) : ProfileEditReducerEvent

    data object ErrorConsumed : ProfileEditReducerEvent
}
