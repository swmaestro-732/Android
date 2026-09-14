package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 후기 전체보기 화면(FS-37) 의 사용자 입력. */
sealed interface CourseReviewListIntent : MviIntent {
    /** 첫 진입. 같은 courseId 로 이미 불러왔으면 다시 조회하지 않는다. */
    data class Load(
        val courseId: Long,
    ) : CourseReviewListIntent

    /** 에러 화면의 "다시 시도". [Load] 의 중복 조회 가드를 건너뛴다. */
    data object Retry : CourseReviewListIntent

    /** 정렬 탭 선택(최신순 / 높은 평점). */
    data class ChangeSort(
        val sort: CourseReviewSort,
    ) : CourseReviewListIntent
}
