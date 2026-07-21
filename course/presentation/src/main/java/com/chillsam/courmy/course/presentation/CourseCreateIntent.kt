package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.course.entity.CoursePlaceVO
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

    /** 장소 검색에서 고른 장소들을 코스에 담는다(중복 id 는 무시). */
    data class AddPlaces(
        val places: List<CoursePlaceVO>,
    ) : CourseCreateIntent

    /** 특정 장소의 "한마디" 메모를 수정한다. */
    data class ChangePlaceNote(
        val placeId: String,
        val note: String,
    ) : CourseCreateIntent

    data class RemovePlace(
        val placeId: String,
    ) : CourseCreateIntent

    data class ChangeVisibility(
        val visibility: CourseVisibility,
    ) : CourseCreateIntent
}
