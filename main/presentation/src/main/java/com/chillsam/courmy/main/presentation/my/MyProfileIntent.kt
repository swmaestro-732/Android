package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface MyProfileIntent : MviIntent {
    /** 화면 진입 시 최초 로드. */
    data object Load : MyProfileIntent

    /** 에러 상태에서 재시도. */
    data object Retry : MyProfileIntent

    /** 코스 목록 끝에 닿아 다음 페이지를 이어 받는다(홈 피드와 같은 방식). */
    data object LoadMore : MyProfileIntent
}
