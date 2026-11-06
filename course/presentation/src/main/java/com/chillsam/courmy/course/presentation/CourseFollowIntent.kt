package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 따라가기 화면 사용자 입력(FS-21). View → ViewModel 단일 진입. */
sealed interface CourseFollowIntent : MviIntent {
    /**
     * 화면 진입 시 따라갈 코스를 지정한다.
     *
     * 이어가기(홈 진행 중인 코스 카드)로 들어오면 [resumeTraceId]·[resumeVisitedCount]·[resumeStartedAt] 가
     * 실려, 미리보기를 건너뛰고 현재 진행도에서 TRACKING 으로 연다. 기본값(0·0·빈 문자열)이면 일반 진입이다.
     */
    data class Load(
        val courseId: Long,
        val resumeTraceId: Long = 0L,
        val resumeVisitedCount: Int = 0,
        val resumeStartedAt: String = "",
    ) : CourseFollowIntent

    data object Retry : CourseFollowIntent

    /** "이 코스 따라가기" — 진행을 시작하고 시작 시각을 기록한다. */
    data object Start : CourseFollowIntent

    /** "도착" — 지금 장소를 방문 처리하고, 마지막이면 완주로 넘어간다. */
    data object CheckArrival : CourseFollowIntent

    data object ConsumeError : CourseFollowIntent
}
