package com.chillsam.courmy.main.presentation

/**
 * 화면/기능 노출 토글. 서버·디자인 준비 전 화면을 숨기거나 드러낼 때 쓴다.
 *
 * 값만 바꾸면 되고 다른 코드는 손대지 않는다.
 */
object FeatureFlags {
    /**
     * 채팅 기능(하단 "채팅" 탭·채팅홈·채팅방) 노출 여부.
     *
     * - true: 하단 네비게이션 가운데에 채팅 탭이 보이고 `/chat`·`/chat/room` 라우트가 등록된다.
     * - false: 하단 탭에서 채팅이 사라지고 채팅 라우트도 등록되지 않는다(딥링크로도 진입 불가).
     */
    const val CHAT_ENABLED = true
}
