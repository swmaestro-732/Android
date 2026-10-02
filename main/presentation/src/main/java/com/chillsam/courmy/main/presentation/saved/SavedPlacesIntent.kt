package com.chillsam.courmy.main.presentation.saved

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 저장함 · 장소 탭 사용자 입력. */
sealed interface SavedPlacesIntent : MviIntent {
    data object Load : SavedPlacesIntent

    data object Retry : SavedPlacesIntent

    /** 목록 끝에 닿았을 때 다음 페이지 요청. 더 없거나 이미 받는 중이면 무시된다. */
    data object LoadMore : SavedPlacesIntent

    /** 저장 취소 확인 다이얼로그에서 "확인". */
    data class Unsave(
        val placeId: Long,
    ) : SavedPlacesIntent

    data object ConsumeError : SavedPlacesIntent
}
