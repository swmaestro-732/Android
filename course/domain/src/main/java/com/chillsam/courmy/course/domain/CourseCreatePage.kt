package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 코스 만들기 화면(FS-34 CourseCreateActivity) 네비게이션 식별자.
 *
 * 인자 없이 들어오면 빈 코스로 시작하고, 임시저장 목록에서 [route] 로 초안의 코스 id 를 실어 오면
 * 그 초안을 불러와 이어서 작성한다. 초안 id 를 화면 밖(공유 상태)에 두지 않고 라우트로 넘기는 이유는,
 * 어느 초안을 편집 중인지가 이 화면 한 번의 진입에만 유효한 값이기 때문이다.
 */
object CourseCreatePage : Page {
    const val PATH = "/courseCreate"
    const val ARG_DRAFT_ID = "draftId"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(draftId: Long): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_DRAFT_ID to draftId.toString()),
        )
}
