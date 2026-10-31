package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 따라가기 화면 사용자 입력(FS-21). View → ViewModel 단일 진입. */
sealed interface CourseFollowIntent : MviIntent {
    /** 화면 진입 시 따라갈 코스를 지정한다. */
    data class Load(
        val courseId: Long,
    ) : CourseFollowIntent

    data object Retry : CourseFollowIntent

    /** "이 코스 따라가기" — 진행을 시작하고 시작 시각을 기록한다. */
    data object Start : CourseFollowIntent

    /** "도착 · 방문 체크" — 지금 장소를 방문 처리하고, 마지막이면 완주로 넘어간다. */
    data object CheckArrival : CourseFollowIntent

    data object ConsumeError : CourseFollowIntent
}
