package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.course.entity.CourseEditPlaceVO
import com.chillsam.courmy.course.entity.CourseVisibility

/** reduce() 에 입력되는 내부 이벤트. ViewModel 이 Intent/코루틴 결과를 이 이벤트로 변환한다. */
sealed interface CourseEditReducerEvent : ReducerEvent {
    data object LoadStarted : CourseEditReducerEvent

    data class Loaded(
        val title: String,
        val description: String,
        val tags: List<String>,
        val places: List<CourseEditPlaceVO>,
        val thumbnailUrl: String,
    ) : CourseEditReducerEvent

    /** 도메인 API 로 읽어 온 현재 공개 설정. null 이면 알 수 없어 기본값을 쓴다. */
    data class VisibilityLoaded(
        val visibility: CourseVisibility?,
    ) : CourseEditReducerEvent

    data class VisibilityChanged(
        val visibility: CourseVisibility,
    ) : CourseEditReducerEvent

    data class LoadFailed(
        val message: String,
    ) : CourseEditReducerEvent

    data class TitleChanged(
        val title: String,
    ) : CourseEditReducerEvent

    data class DescriptionChanged(
        val description: String,
    ) : CourseEditReducerEvent

    data class PlacesChanged(
        val places: List<CourseEditPlaceVO>,
    ) : CourseEditReducerEvent

    data object SaveStarted : CourseEditReducerEvent

    data object Saved : CourseEditReducerEvent

    data class SaveFailed(
        val message: String,
    ) : CourseEditReducerEvent

    data object ErrorDismissed : CourseEditReducerEvent
}
