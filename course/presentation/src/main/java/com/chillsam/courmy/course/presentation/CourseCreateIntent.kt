package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.course.entity.CourseVisibility

/**
 * 코스 만들기 화면 사용자 입력. View → ViewModel 단일 진입.
 */
sealed interface CourseCreateIntent : MviIntent {
    data object Load : CourseCreateIntent

    data class ChangeName(
        val name: String,
    ) : CourseCreateIntent

    data class ChangeDescription(
        val description: String,
    ) : CourseCreateIntent

    data class AddTag(
        val tag: String,
    ) : CourseCreateIntent

    data class RemoveTag(
        val tag: String,
    ) : CourseCreateIntent

    data class RemovePlace(
        val placeId: String,
    ) : CourseCreateIntent

    data class ChangeVisibility(
        val visibility: CourseVisibility,
    ) : CourseCreateIntent
}
