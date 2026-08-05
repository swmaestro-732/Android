package com.chillsam.courmy.main.presentation.saved

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.saved.SavedCourseVO

/** 저장함 · 코스 탭의 내부 상태 변경 이벤트. */
sealed interface SavedCoursesReducerEvent : ReducerEvent {
    data object LoadStarted : SavedCoursesReducerEvent

    data class Loaded(
        val courses: List<SavedCourseVO>,
    ) : SavedCoursesReducerEvent

    data class LoadFailed(
        val message: String,
    ) : SavedCoursesReducerEvent

    /** 저장 취소 성공 — 목록에서 해당 코스를 뺀다(재조회 없이 즉시 반영). */
    data class Unsaved(
        val courseId: String,
    ) : SavedCoursesReducerEvent

    data class UnsaveFailed(
        val message: String,
    ) : SavedCoursesReducerEvent

    data object ErrorConsumed : SavedCoursesReducerEvent
}
