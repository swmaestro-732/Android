package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.entity.CourseVisibility

/**
 * reduce() 에 입력되는 내부 이벤트. ViewModel 이 Intent/코루틴 결과를 이 이벤트로 변환한다.
 */
sealed interface CourseCreateReducerEvent : ReducerEvent {
    data object LoadStarted : CourseCreateReducerEvent

    data class DraftLoaded(
        val draft: CourseDraftVO,
    ) : CourseCreateReducerEvent

    data class NameChanged(
        val name: String,
    ) : CourseCreateReducerEvent

    data class DescriptionChanged(
        val description: String,
    ) : CourseCreateReducerEvent

    data class ThumbnailPhotosChanged(
        val photoUrls: List<String>,
    ) : CourseCreateReducerEvent

    data class TagsChanged(
        val tags: List<String>,
    ) : CourseCreateReducerEvent

    data class PlacesChanged(
        val places: List<CoursePlaceVO>,
    ) : CourseCreateReducerEvent

    data class VisibilityChanged(
        val visibility: CourseVisibility,
    ) : CourseCreateReducerEvent
}
