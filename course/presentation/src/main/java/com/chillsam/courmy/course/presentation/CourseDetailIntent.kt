package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/**
 * 코스 상세 화면 사용자 입력. View → ViewModel 단일 진입.
 */
sealed interface CourseDetailIntent : MviIntent {
    /** 화면 진입 시 조회할 코스를 지정한다. 같은 코스로 다시 들어오면 재조회하지 않는다. */
    data class Load(
        val courseId: Long,
    ) : CourseDetailIntent

    data object Retry : CourseDetailIntent

    /** 하단 "코스 저장하기" 탭 — 현재 상태의 반대로 요청한다. */
    data object ToggleSave : CourseDetailIntent

    /** 내 코스 삭제(확인 다이얼로그를 거친 뒤). */
    data object Delete : CourseDetailIntent

    data object ConsumeError : CourseDetailIntent
}
