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
    val isSaving: Boolean = false,
    /** 저장 실패처럼 토스트로 한 번 알리고 지우는 일시 오류. */
    val actionErrorMessage: String? = null,
    val isDeleting: Boolean = false,
    /** 삭제가 끝나 화면을 닫아도 되는 상태. */
    val isDeleted: Boolean = false,
) : UiState {
    companion object {
        val empty: CourseDetailUIState = CourseDetailUIState()
    }
}
