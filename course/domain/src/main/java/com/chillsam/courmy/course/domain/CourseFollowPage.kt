package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 코스 따라가기 화면(FS-21). 코스 상세의 "따라가기" 버튼으로 진입하며 [route] 로 courseId 를 싣는다.
 *
 * 동선 미리보기 → 진행(도착·방문 체크) → 완주(통계·후기 유도)를 한 라우트 안에서 단계로 오간다 —
 * 단계마다 라우트를 가르면 진행 중 상태(방문 기록·시작 시각)를 화면 간에 넘겨야 해서다.
 */
object CourseFollowPage : Page {
    const val PATH = "/course/follow"
    const val ARG_COURSE_ID = "courseId"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(courseId: Long): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_COURSE_ID to courseId.toString()),
        )
}
