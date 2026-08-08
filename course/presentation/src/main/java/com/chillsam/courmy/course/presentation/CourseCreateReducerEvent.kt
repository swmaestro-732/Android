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

    /** 초안 로드 실패. 스피너를 걷고 안내를 띄운다(빈 초안으로 계속 쓸 수 있게 한다). */
    data class LoadFailed(
        val message: String,
    ) : CourseCreateReducerEvent

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

    data object SaveStarted : CourseCreateReducerEvent

    data class SaveSucceeded(
        val courseId: Long,
        /** 사진 업로드가 모두 성공했는지. false 면 코스는 만들어졌지만 사진이 빠졌다. */
        val imagesUploaded: Boolean,
    ) : CourseCreateReducerEvent

    data class SaveFailed(
        val message: String,
    ) : CourseCreateReducerEvent

    data object SaveErrorConsumed : CourseCreateReducerEvent

    data class StepChanged(
        val step: Int,
    ) : CourseCreateReducerEvent

    data class SuggestedTagsLoaded(
        val tags: List<String>,
    ) : CourseCreateReducerEvent

    data object SavedConsumed : CourseCreateReducerEvent
}
