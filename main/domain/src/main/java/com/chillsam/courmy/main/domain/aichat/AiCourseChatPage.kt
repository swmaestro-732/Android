package com.chillsam.courmy.main.domain.aichat

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 커미 AI 코스 추천 채팅 화면 식별자.
 *
 * 홈(FS-09)의 "커미 AI" 배너("코스 추천받기")에서 진입한다.
 * 사용자가 AI 와 문답하며 취향을 좁혀 코스를 추천받는 대화형 화면이다.
 *
 * 현재는 AI 백엔드 계약이 없어 프레젠테이션 레이어가 스크립트 기반 목업 대화를 구동한다.
 * 실제 추천 API 가 붙으면 UseCase 로 교체하고 data/entity 레이어를 승격한다. [wiki-needed]
 */
object AiCourseChatPage : Page {
    const val PATH = "/ai/course-chat"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
