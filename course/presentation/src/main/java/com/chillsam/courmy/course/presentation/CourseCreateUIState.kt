package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.entity.CourseVisibility
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 코스 만들기 화면 상태(Figma FS-34). 컬렉션은 Compose 안정성을 위해 Immutable* 사용.
 */
data class CourseCreateUIState(
    val isLoading: Boolean = true,
    val name: String = "",
    val description: String = "",
    val tags: ImmutableList<String> = persistentListOf(),
    val suggestedTags: ImmutableList<String> = persistentListOf(),
    val places: ImmutableList<CoursePlaceVO> = persistentListOf(),
    val visibility: CourseVisibility = CourseVisibility.PUBLIC,
) : UiState {
    companion object {
        val empty: CourseCreateUIState = CourseCreateUIState()
    }
}
