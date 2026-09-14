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
    /** 비로그인 상태로 팔로우를 눌러 로그인 안내를 띄워야 하는 상태. */
    val needsLogin: Boolean = false,
    val isDeleting: Boolean = false,
    /** 삭제가 끝나 화면을 닫아도 되는 상태. */
    val isDeleted: Boolean = false,
    val social: CourseSocialUIState = CourseSocialUIState(),
) : UiState {
    companion object {
        val empty: CourseDetailUIState = CourseDetailUIState()
    }
}

/** 서버 계약 전까지 상세 화면 생명주기 동안 유지하는 좋아요·댓글 상태. */
data class CourseSocialUIState(
    val isLiked: Boolean = false,
    val likeCount: Int = 128,
    val commentDraft: String = "",
    val comments: List<CourseCommentItem> = emptyList(),
)

/** 코스 상세 댓글 표시 모델. API 계약 확정 전 presentation 상태에만 둔다. */
data class CourseCommentItem(
    val id: Long,
    val author: String,
    val body: String,
    val dateText: String,
)
