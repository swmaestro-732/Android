package com.chillsam.courmy.main.presentation.saved

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.saved.SavedCourseVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 저장함 · 코스 탭 상태.
 * [errorMessage] 는 저장 취소 실패처럼 토스트로 한 번 알리고 지우는 일시 오류다
 * (목록 로드 실패는 [loadErrorMessage] 로 화면에 남긴다).
 */
data class SavedCoursesUIState(
    val isLoading: Boolean = true,
    val courses: ImmutableList<SavedCourseVO> = persistentListOf(),
    val loadErrorMessage: String? = null,
    val errorMessage: String? = null,
) : UiState {
    companion object {
        val empty: SavedCoursesUIState = SavedCoursesUIState()
    }
}
