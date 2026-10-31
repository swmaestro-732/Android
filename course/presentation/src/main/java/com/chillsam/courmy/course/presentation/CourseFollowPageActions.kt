package com.chillsam.courmy.course.presentation

/** 따라가기 페이지가 앱 네비게이션 계층에 위임하는 동작 모음. */
data class CourseFollowPageActions(
    val onBack: () -> Unit,
    /** 완주 후 "리뷰 남기기" — 코스 후기 화면(FS-37)으로 보낸다. 작성은 그 화면의 오버레이가 맡는다. */
    val onLeaveReview: () -> Unit,
)
