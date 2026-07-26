package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CourseDetailVO

/**
 * 코스 상세 화면 상태(Figma FS-11).
 * [detail] 이 있으면 화면을 렌더하고, 없으면 [isLoading]/[errorMessage] 로 로딩·에러를 표시한다.
 */
data class CourseDetailUIState(
    val isLoading: Boolean = true,
    val detail: CourseDetailVO? = null,
    val errorMessage: String? = null,
) : UiState {
    companion object {
        val empty: CourseDetailUIState = CourseDetailUIState()
    }
}
