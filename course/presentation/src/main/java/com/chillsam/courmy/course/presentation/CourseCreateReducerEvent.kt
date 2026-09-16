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

    /**
     * 작성 화면에 채울 초안. [courseId] 는 이어서 작성 중인 서버 초안 id 이며, 새 코스면 null 이다.
     * 이 값이 그대로 임시저장이 `POST` 로 갈지 `PATCH` 로 갈지를 정한다.
     */
    data class DraftLoaded(
        val draft: CourseDraftVO,
        val courseId: Long?,
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

    data object DraftSaveStarted : CourseCreateReducerEvent

    /**
     * 임시저장 성공. [courseId] 를 세션에 남겨, 이어서 또 임시저장하면 같은 초안을 갱신하게 한다.
     * 남기지 않으면 누를 때마다 새 초안이 만들어진다.
     */
    data class DraftSaveSucceeded(
        val courseId: Long,
        /** 사진 업로드가 모두 성공했는지. false 면 초안은 저장됐지만 사진이 빠졌다. */
        val imagesUploaded: Boolean,
    ) : CourseCreateReducerEvent

    /** 임시저장 실패(서버 오류 + 장소 수 미달 같은 앱 자체 판정). */
    data class DraftSaveFailed(
        val message: String,
    ) : CourseCreateReducerEvent

    data object DraftSavedConsumed : CourseCreateReducerEvent
}
