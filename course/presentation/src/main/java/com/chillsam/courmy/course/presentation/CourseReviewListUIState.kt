package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 후기 전체보기 정렬 기준.
 *
 * 서버에 정렬 파라미터가 없어 **받아 온 목록을 클라이언트에서 정렬**한다.
 * [LATEST] 는 별도 정렬 없이 서버가 준 순서를 그대로 쓴다 — 그 순서가 최신순이라는 전제다.
 * 서버가 정렬을 지원하면 이 enum 을 쿼리 파라미터로 넘기고 아래 정렬 로직을 지운다.
 */
enum class CourseReviewSort(
    val label: String,
) {
    LATEST("최신순"),
    RATING("높은 평점"),
}

/**
 * 후기 전체보기 화면 상태(FS-37).
 *
 * [summary] 가 있으면 화면을 렌더하고, 없으면 [isLoading]/[errorMessage] 로 로딩·에러를 표시한다.
 * [reviews] 는 [summary] 의 목록에 [sort] 를 적용한 결과로, 화면이 그대로 그리도록 미리 정렬해 둔다.
 */
data class CourseReviewListUIState(
    val isLoading: Boolean = true,
    val summary: CourseReviewListVO? = null,
    val reviews: ImmutableList<CourseReviewVO> = persistentListOf(),
    val sort: CourseReviewSort = CourseReviewSort.LATEST,
    val errorMessage: String? = null,
) : UiState {
    companion object {
        val empty: CourseReviewListUIState = CourseReviewListUIState()
    }
}
