package com.chillsam.courmy.main.presentation.saved

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 저장함 · 코스 탭(FS-14) 사용자 입력. */
sealed interface SavedCoursesIntent : MviIntent {
    data object Load : SavedCoursesIntent

    data object Retry : SavedCoursesIntent

    /** 목록 끝에 닿았을 때 다음 페이지 요청. 더 없거나 이미 받는 중이면 무시된다. */
    data object LoadMore : SavedCoursesIntent

    /** 저장 취소 확인 다이얼로그에서 "확인". */
    data class Unsave(
        val courseId: String,
    ) : SavedCoursesIntent

    data object ConsumeError : SavedCoursesIntent
}
