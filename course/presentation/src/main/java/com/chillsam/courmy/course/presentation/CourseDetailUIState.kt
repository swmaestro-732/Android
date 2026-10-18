package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CourseDetailVO

/**
 * 코스 상세 화면 상태(Figma FS-11).
 * [detail] 이 있으면 화면을 렌더하고, 없으면 [isLoading]/[errorMessage] 로 로딩·에러를 표시한다.
 */
data class CourseDetailUIState(
    val isLoading: Boolean = true,
    val detail: CourseDetailVO? = null,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    /** 작성자 팔로우 요청 중. 중복 탭을 막는다. */
    val isFollowing: Boolean = false,
    /** 저장 실패처럼 토스트로 한 번 알리고 지우는 일시 오류. */
    val actionErrorMessage: String? = null,
    /** 비로그인 상태로 팔로우·포크를 눌러 로그인 안내를 띄워야 하는 상태. */
    val needsLogin: Boolean = false,
    /** 포크 진입이 확정돼 코스 만들기로 이동해도 되는 상태. 이동을 끝내면 지운다. */
    val isForkRequested: Boolean = false,
    val isDeleting: Boolean = false,
    /** 삭제가 끝나 화면을 닫아도 되는 상태. */
    val isDeleted: Boolean = false,
    val social: CourseSocialUIState = CourseSocialUIState(),
) : UiState {
    companion object {
        val empty: CourseDetailUIState = CourseDetailUIState()
    }
}

/**
 * 좋아요·댓글 상태. 좋아요 여부는 상세 응답(viewer.hasLiked)으로 시딩하고 토글은 서버와 연동된다.
 *
 * [likeCount] 가 null 이면 **집계를 아직 모르는 상태**다 — 상세 조회(BFF)가 좋아요 수를 주지 않아
 * 토글 응답(likesCnt)을 받기 전까지는 숫자를 그리지 않는다. 지어낸 수를 보여주지 않기 위해서다.
 * TODO-API-SPEC: 상세 응답에 좋아요 집계가 생기면 진입 시점에 시딩한다. [wiki-needed]

 */
data class CourseSocialUIState(
    val isLiked: Boolean = false,
    val likeCount: Int? = null,
)
