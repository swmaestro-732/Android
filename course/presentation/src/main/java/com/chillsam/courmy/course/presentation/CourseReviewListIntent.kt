package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.course.entity.CourseReviewSort

/** 후기 전체보기 화면(FS-37) 의 사용자 입력. */
sealed interface CourseReviewListIntent : MviIntent {
    /** 첫 진입. 같은 courseId 로 이미 불러왔으면 다시 조회하지 않는다. */
    data class Load(
        val courseId: Long,
    ) : CourseReviewListIntent

    /** 에러 화면의 "다시 시도". [Load] 의 중복 조회 가드를 건너뛴다. */
    data object Retry : CourseReviewListIntent

    /** 정렬 탭 선택(최신순 / 높은 평점). 서버 정렬이라 목록을 처음부터 다시 받는다. */
    data class ChangeSort(
        val sort: CourseReviewSort,
    ) : CourseReviewListIntent

    /** 목록 끝에 닿아 다음 페이지를 이어받는다. */
    data object LoadMore : CourseReviewListIntent

    /** 내 후기 삭제. */
    data class DeleteReview(
        val reviewId: Long,
    ) : CourseReviewListIntent

    /**
     * 후기를 새로 쓰고 돌아왔다. 방금 쓴 글이 목록에 보이도록 처음부터 다시 받는다
     * (집계·분포도 함께 바뀐다).
     */
    data object ReloadAfterWrite : CourseReviewListIntent

    /** 스낵바 안내를 닫았다. */
    data object ConsumeMessage : CourseReviewListIntent
}
