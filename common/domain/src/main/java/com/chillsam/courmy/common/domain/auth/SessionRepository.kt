package com.chillsam.courmy.common.domain.auth

/**
 * 로그인 세션 조회.
 *
 * 팔로우처럼 로그인이 필요한 동작을 누르기 전에 확인해, 401 을 받고 나서 알리는 대신
 * 미리 안내할 수 있게 한다. 세션의 실제 소유자는 `common:data` 의 TokenStore 다.
 *
 * feature 를 가리지 않고 필요해 common 에 둔다 —
 * `main:domain` 의 AuthRepository 는 로그인·가입 절차까지 다루고 course 에서는 닿지 않는다.
 */
interface SessionRepository {
    /** 지금 로그인 상태인지(액세스 토큰 보유 여부). */
    val isLoggedIn: Boolean
}
