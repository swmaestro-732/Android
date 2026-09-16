package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 홈 공개 코스 피드(FS-09) 사용자 입력. */
sealed interface HomeFeedIntent : MviIntent {
    data object Load : HomeFeedIntent

    data object Retry : HomeFeedIntent

    /** 목록 끝에 닿았을 때 다음 페이지 요청. 더 없거나 이미 받는 중이면 무시된다. */
    data object LoadMore : HomeFeedIntent

    /** 카드 북마크 탭 — 저장/저장 취소를 토글한다. */
    data class ToggleSave(
        val courseId: String,
    ) : HomeFeedIntent

    data object ConsumeError : HomeFeedIntent
}
