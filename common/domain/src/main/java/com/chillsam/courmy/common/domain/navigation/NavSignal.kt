package com.chillsam.courmy.common.domain.navigation

/**
 * 단일 네비게이션 플로우에 흘려보내는 신호.
 *
 * - [GoToDestPage]: 앱 내 전진 이동(탭/아이템 클릭 등). 현재 백스택 위로 push.
 * - [DeepLink]: 앱 실행 중(웜 스타트) 도착한 deep-link. 기존 스택은 보존하고 대상만 최전면으로(bring-to-front).
 *   콜드 스타트의 synthetic 부모 체인과 달리 사용자의 현재 맥락을 유지하는 것이 의도된 동작이다.
 * - [Replace]: 백스택을 대상 화면 하나로 교체(스플래시/온보딩처럼 뒤로 돌아오면 안 되는 진입 흐름).
 * - [Back]: 시스템/하드웨어 백 키와 동일하게 한 단계 뒤로 이동.
 */
sealed interface NavSignal {
    data class GoToDestPage(
        val route: NavRoute,
        val source: NavigationSource? = null,
    ) : NavSignal

    data class DeepLink(
        val route: NavRoute,
        val source: NavigationSource? = null,
    ) : NavSignal

    data class Replace(
        val route: NavRoute,
        val source: NavigationSource? = null,
    ) : NavSignal

    data object Back : NavSignal

    data object AuthenticationCompleted : NavSignal

    /**
     * 로그인 화면에서 "게스트로 돌아가기". 로그인 바로 앞이 온보딩/스플래시면(온보딩 건너뛰기·로그인하기로
     * 올라온 경로) 돌아갈 게스트 홈이 없어, 단순 Back 은 온보딩→로그인 무한 루프가 된다. 그 경우 홈(게스트)으로
     * 교체하고, 게스트 홈에서 올라온 경우엔 그 화면으로 복귀한다.
     */
    data object GuestBrowsingContinued : NavSignal
}

/** 사용자가 누른 컨트롤의 고정 식별자. 입력값·닉네임·URL을 담지 않는다. */
data class NavigationSource(
    val screen: String,
    val button: String,
    val trigger: String = "button",
)
