package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CourseReviewLimits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

/**
 * 후기 작성 화면 상태.
 *
 * [photoUris] 는 아직 **로컬 URI**(`content://`)다. 업로드는 제출할 때 한 번에 하므로
 * 화면은 고른 사진을 그대로 들고 있다가 넘긴다.
 *
 * [submittedReviewId] 가 0 이 아니면 작성이 끝난 상태다 — 호출부가 이걸 보고 화면을 닫는다.
 */
data class CourseReviewWriteUIState(
    val rating: Int = 0,
    val content: String = "",
    val photoUris: ImmutableList<String> = persistentListOf(),
    val selectedTagCodes: ImmutableSet<String> = persistentSetOf(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val submittedReviewId: Long = 0L,
) : UiState {
    /** 별점은 필수다(서버 `@Min(1)`). 본문·사진·태그는 없어도 보낼 수 있다. */
    val canSubmit: Boolean
        get() = rating >= CourseReviewLimits.MIN_RATING && !isSubmitting

    /** 태그를 더 고를 수 있는지. 상한에 닿으면 안 고른 칩을 눌러도 반응하지 않는다. */
    val canSelectMoreTags: Boolean
        get() = selectedTagCodes.size < CourseReviewLimits.MAX_TAGS

    companion object {
        val empty: CourseReviewWriteUIState = CourseReviewWriteUIState()
    }
}
