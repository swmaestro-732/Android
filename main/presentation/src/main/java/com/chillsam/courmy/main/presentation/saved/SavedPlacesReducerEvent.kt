package com.chillsam.courmy.main.presentation.saved

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.saved.SavedPlaceVO

/** 저장함 · 장소 탭의 내부 상태 변경 이벤트. */
sealed interface SavedPlacesReducerEvent : ReducerEvent {
    data class CategorySelected(
        val category: String?,
    ) : SavedPlacesReducerEvent

    data class VisitedSelected(
        val visited: Boolean,
    ) : SavedPlacesReducerEvent

    data object LoadStarted : SavedPlacesReducerEvent

    data class Loaded(
        val places: List<SavedPlaceVO>,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : SavedPlacesReducerEvent

    data object LoadMoreStarted : SavedPlacesReducerEvent

    /** 다음 페이지 도착. 기존 목록 뒤에 이어 붙인다. */
    data class MoreLoaded(
        val places: List<SavedPlaceVO>,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : SavedPlacesReducerEvent

    /** 다음 페이지 실패. 보고 있는 목록은 그대로 두고 안내만 띄운다. */
    data class MoreFailed(
        val message: String,
    ) : SavedPlacesReducerEvent

    data class LoadFailed(
        val message: String,
    ) : SavedPlacesReducerEvent

    /** 저장 취소 성공 — 목록에서 해당 장소를 뺀다(재조회 없이 즉시 반영). */
    data class Unsaved(
        val placeId: Long,
    ) : SavedPlacesReducerEvent

    data class UnsaveFailed(
        val message: String,
    ) : SavedPlacesReducerEvent

    data object ErrorConsumed : SavedPlacesReducerEvent
}
