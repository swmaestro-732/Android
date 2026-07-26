package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/**
 * 코스 상세 화면 사용자 입력. View → ViewModel 단일 진입.
 */
sealed interface CourseDetailIntent : MviIntent {
    data object Load : CourseDetailIntent

    data object Retry : CourseDetailIntent
}
