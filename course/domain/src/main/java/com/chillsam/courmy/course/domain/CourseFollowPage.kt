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

    /** 이어가기 인자. 홈 "진행 중인 코스" 카드에서만 싣는다 — 진행 중 세션을 현재 진행도로 바로 연다. */
    const val ARG_TRACE_ID = "traceId"
    const val ARG_VISITED_COUNT = "visited"
    const val ARG_STARTED_AT = "startedAt"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(courseId: Long): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_COURSE_ID to courseId.toString()),
        )

    /**
     * 이어가기용 진입. 진행 중 세션([traceId])과 방문 수([visitedPlaceCount])·시작 시각([startedAt])을 실어,
     * 미리보기를 건너뛰고 현재 진행도에서 TRACKING 으로 연다. 방문 수가 0 이거나 traceId 가 없으면
     * 일반 진입과 같다(미리보기부터).
     */
    fun route(
        courseId: Long,
        traceId: Long,
        visitedPlaceCount: Int,
        startedAt: String,
    ): NavRoute =
        NavRoute(
            path = PATH,
            args =
                buildMap {
                    put(ARG_COURSE_ID, courseId.toString())
                    if (traceId > 0 && visitedPlaceCount > 0) {
                        put(ARG_TRACE_ID, traceId.toString())
                        put(ARG_VISITED_COUNT, visitedPlaceCount.toString())
                        if (startedAt.isNotBlank()) put(ARG_STARTED_AT, startedAt)
                    }
                },
        )
}
