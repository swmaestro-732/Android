package com.chillsam.courmy.common.domain.session

import kotlinx.coroutines.flow.Flow

/**
 * 세션 만료(refresh 토큰 재발급 실패 등)를 앱 전역에 알리는 버스.
 * data 레이어(토큰 재발급 실패 지점)가 [notifyExpired] 로 방출하고, presentation 이 [expirations] 를
 * 관찰해 로그인 화면으로 유도한다. 실제 토큰은 여기서 다루지 않는다.
 */
interface SessionEventBus {
    /** 세션이 만료되어 재로그인이 필요할 때 방출되는 1회성 신호. */
    val expirations: Flow<Unit>

    /** 세션 만료를 알린다(동기 호출 가능). */
    fun notifyExpired()
}
