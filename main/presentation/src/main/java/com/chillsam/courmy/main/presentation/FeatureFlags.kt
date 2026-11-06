package com.chillsam.courmy.main.presentation

/**
 * 화면/기능 노출 토글. 서버·디자인 준비 전 화면을 숨기거나 드러낼 때 쓴다.
 *
 * 값만 바꾸면 되고 다른 코드는 손대지 않는다.
 */
object FeatureFlags {
    /** 검색의 테마 추천 바로가기 노출 여부. true일 때 해당 테마의 오늘의 추천 코스로 이동한다. */
    const val SEARCH_THEME_BROWSE_ENABLED = false

    /**
     * 채팅 기능(하단 "채팅" 탭·채팅홈·채팅방) 노출 여부.
     *
     * - true: 하단 네비게이션 가운데에 채팅 탭이 보이고 `/chat`·`/chat/room` 라우트가 등록된다.
     * - false: 하단 탭에서 채팅이 사라지고 채팅 라우트도 등록되지 않는다(딥링크로도 진입 불가).
     *
     * 아직 백엔드·노출 준비가 안 돼 repo 는 false 로 둔다(로컬 데모에서만 true 로 켜 확인).
     */
    const val CHAT_ENABLED = false

    /**
     * 홈 "지역별 둘러보기" 타일 지도 노출 여부.
     *
     * - true: 테마별 둘러보기 아래에 시·도 타일 지도가 보이고, 지역 탭 시 "{지역} 코스"로 이동한다.
     * - false: 섹션 전체를 숨긴다. 지역별 코스 필터는 백엔드 선행이라 서버 준비 전 숨기고 싶을 때 쓴다.
     */
    const val REGION_BROWSE_ENABLED = false
}
