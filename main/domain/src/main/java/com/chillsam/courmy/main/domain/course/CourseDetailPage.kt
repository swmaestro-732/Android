package com.chillsam.courmy.main.domain.course

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 코스 상세 화면(이름 + 생성 시각만). 저장한 코스 목록에서 항목을 선택해 진입한다.
 * 상세에 필요한 값은 [route] 로 인자에 실어 전달한다.
 */
object CourseDetailPage : Page {
    const val PATH = "/course/detail"
    const val ARG_TITLE = "title"
    const val ARG_CREATED_AT = "createdAt"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(
        title: String,
        createdAtMillis: Long,
    ): NavRoute =
        NavRoute(
            path = PATH,
            args =
                mapOf(
                    ARG_TITLE to title,
                    ARG_CREATED_AT to createdAtMillis.toString(),
                ),
        )
}
