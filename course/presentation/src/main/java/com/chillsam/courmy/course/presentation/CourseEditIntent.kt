package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.course.entity.CourseVisibility

/** 코스 편집 화면의 사용자 입력. */
sealed interface CourseEditIntent : MviIntent {
    /** 화면 진입 시 코스 상세를 불러와 편집 값을 채운다. */
    data object Load : CourseEditIntent

    data class ChangeTitle(
        val title: String,
    ) : CourseEditIntent

    data class ChangeDescription(
        val description: String,
    ) : CourseEditIntent

    data class ChangeVisibility(
        val visibility: CourseVisibility,
    ) : CourseEditIntent

    /** 장소별 한마디 수정. 장소 구성은 못 바꾸므로 [placeId] 로 대상만 찾는다. */
    data class ChangePlaceTip(
        val placeId: Long,
        val tip: String,
    ) : CourseEditIntent

    data object Save : CourseEditIntent

    /** 저장 실패 안내를 닫는다. */
    data object DismissError : CourseEditIntent
}
