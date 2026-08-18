package com.chillsam.courmy.common.domain.network

/**
 * 네트워크 연결 조회.
 *
 * 이 앱은 화면 대부분이 서버 응답으로 채워져 오프라인에서는 빈 화면과 실패 토스트만 남는다.
 * 그래서 스플래시에서 한 번 확인하고, 연결이 없으면 진입 자체를 막는다.
 */
interface ConnectivityRepository {
    /**
     * 지금 인터넷에 닿을 수 있는지.
     *
     * 연결된 네트워크가 있는지에 더해 **실제로 인터넷이 검증됐는지**까지 본다 —
     * 캡티브 포털이나 데이터가 끊긴 와이파이는 붙어 있어도 요청이 나가지 않는다.
     */
    fun isOnline(): Boolean
}
