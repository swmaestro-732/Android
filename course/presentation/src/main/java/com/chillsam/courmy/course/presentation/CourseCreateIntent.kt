package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.course.entity.CourseCompleteVO
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

    /** 코스 썸네일 사진 목록을 교체한다(추가·삭제 공통). 최대 개수 초과분은 잘라낸다. */
    data class ChangeThumbnailPhotos(
        val photoUrls: List<String>,
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

    /** 특정 장소의 사진 목록을 교체한다(추가·삭제 공통). 최대 개수 초과분은 잘라낸다. */
    data class ChangePlacePhotos(
        val placeId: String,
        val photoUrls: List<String>,
    ) : CourseCreateIntent

    data class RemovePlace(
        val placeId: String,
    ) : CourseCreateIntent

    /** 담은 장소의 순서를 [fromIndex] 에서 [toIndex] 로 바꾼다(드래그 재정렬). */
    data class MovePlace(
        val fromIndex: Int,
        val toIndex: Int,
    ) : CourseCreateIntent

    data class ChangeVisibility(
        val visibility: CourseVisibility,
    ) : CourseCreateIntent

    /** 임시저장 버튼: 현재 작성 중인 초안 전체를 세션에 임시저장한다. */
    data object SaveDraft : CourseCreateIntent

    /** 코스 저장 완료: 완성 데이터를 보관하고 저장 목록에 추가한다. */
    data class CompleteCourse(
        val course: CourseCompleteVO?,
    ) : CourseCreateIntent

    /** 저장 실패 안내를 노출한 뒤 상태에서 지운다. */
    data object ConsumeSaveError : CourseCreateIntent

    /** 다음 단계로. 마지막 단계에서는 쓰지 않는다(저장은 [CompleteCourse]). */
    data object NextStep : CourseCreateIntent

    /** 이전 단계로. 첫 단계에서는 아무 일도 하지 않는다. */
    data object PrevStep : CourseCreateIntent

    /** 저장 성공 신호를 소비한다(화면 이동을 끝낸 뒤 호출). 남겨 두면 재진입 때 다시 이동한다. */
    data object ConsumeSaved : CourseCreateIntent
}
