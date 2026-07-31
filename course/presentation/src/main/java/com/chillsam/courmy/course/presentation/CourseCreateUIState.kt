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
    val thumbnailPhotos: ImmutableList<String> = persistentListOf(),
    val tags: ImmutableList<String> = persistentListOf(),
    val suggestedTags: ImmutableList<String> = persistentListOf(),
    val places: ImmutableList<CoursePlaceVO> = persistentListOf(),
    val visibility: CourseVisibility = CourseVisibility.PUBLIC,
) : UiState {
    /**
     * 코스 저장 가능 최소 조건: 코스 이름 有 · 장소 [MIN_PLACES]곳 이상 · 각 장소마다 사진 1장 이상.
     * 미충족 시 저장 버튼을 비활성화한다.
     */
    val canSave: Boolean
        get() =
            name.isNotBlank() &&
                places.size >= MIN_PLACES &&
                places.all { it.photoUrls.isNotEmpty() }

    companion object {
        val empty: CourseCreateUIState = CourseCreateUIState()

        /** 썸네일(코스 대표 사진)은 1장만 선택한다. */
        const val MAX_THUMBNAIL_PHOTOS = 1

        /** 코스 저장에 필요한 최소 장소 수. */
        const val MIN_PLACES = 2
    }
}
