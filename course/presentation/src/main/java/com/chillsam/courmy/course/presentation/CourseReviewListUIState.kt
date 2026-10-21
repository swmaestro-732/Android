package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort
import com.chillsam.courmy.course.entity.CourseReviewVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 후기 전체보기 화면 상태(FS-37).
 *
 * [summary] 가 있으면 화면을 렌더하고, 없으면 [isLoading]/[errorMessage] 로 로딩·에러를 표시한다.
 * [summary] 는 **첫 페이지**의 집계(평균·분포·사진 띠)를 들고 있고, [reviews] 는 이어받은 페이지까지
 * 누적한 목록이다 — 집계는 페이지마다 같은 값이라 덮어쓸 이유가 없고, 사진 띠가 스크롤할 때마다
 * 바뀌면 화면이 튄다.
 *
 * 정렬은 서버가 하므로 [sort] 가 바뀌면 목록을 처음부터 다시 받는다([reviews] 도 비운다).
 */
data class CourseReviewListUIState(
    val isLoading: Boolean = true,
    val summary: CourseReviewListVO? = null,
    val reviews: ImmutableList<CourseReviewVO> = persistentListOf(),
    val sort: CourseReviewSort = CourseReviewSort.LATEST,
    val errorMessage: String? = null,
    /** 다음 페이지 커서. null 이면 이어받을 것이 없다. */
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    /** 다음 페이지를 받는 중. 목록 하단 스피너와 중복 요청 가드에 쓴다. */
    val isLoadingMore: Boolean = false,
    /** 삭제 진행 중인 리뷰 id. 0 이면 삭제 중이 아니다(중복 탭 방지). */
    val deletingReviewId: Long = 0L,
    /** 스낵바로 한 번만 보여줄 안내(삭제 결과·작성 불가 사유 등). */
    val message: String? = null,
    /** 로그인 상태. 작성은 액세스 토큰이 필요하다(서버 `@AccessTokenRequired`). */
    val isLoggedIn: Boolean = false,
) : UiState {
    /**
     * 후기 작성 버튼을 띄울지.
     *
     * 비로그인에게는 감춘다 — 다 쓰고 제출한 뒤에야 401 로 막히면 쓴 내용을 잃는다.
     * 이미 쓴 후기가 **받아 온 범위 안에** 있어도 감춘다. 뒤쪽 페이지에 있으면 여기서 못 걸러
     * 버튼이 보일 수 있는데, 그때는 서버가 409 로 막고 작성 화면이 사유를 안내한다.
     */
    val canWriteReview: Boolean get() = isLoggedIn && summary != null && reviews.none { it.isMine }

    companion object {
        val empty: CourseReviewListUIState = CourseReviewListUIState()
    }
}
