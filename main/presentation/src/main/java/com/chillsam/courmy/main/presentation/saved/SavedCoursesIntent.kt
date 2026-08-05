package com.chillsam.courmy.main.presentation.saved

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 저장함 · 코스 탭(FS-14) 사용자 입력. */
sealed interface SavedCoursesIntent : MviIntent {
    data object Load : SavedCoursesIntent

    data object Retry : SavedCoursesIntent

    /** 저장 취소 확인 다이얼로그에서 "확인". */
    data class Unsave(
        val courseId: String,
    ) : SavedCoursesIntent

    data object ConsumeError : SavedCoursesIntent
}
