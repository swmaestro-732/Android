package com.chillsam.courmy.common.data.telemetry

/**
 * Amplitude 이벤트 전송 계약.
 *
 * Amplitude SDK 의존성을 app 모듈에만 가두기 위해 data 레이어에는 이 인터페이스만 둔다.
 * 실제 구현(API 키가 있으면 SDK, 없으면 [NoOpAmplitudeClient])은 app 모듈의 Hilt 모듈이 바인딩한다.
 *
 * **여기에 넘기는 값에 개인정보·토큰·서버 응답 본문을 담지 않는다.** 이벤트는 외부(Amplitude)에 저장된다.
 */
interface AmplitudeClient {
    fun track(
        eventName: String,
        properties: Map<String, String> = emptyMap(),
    )
}

/** API 키가 없거나 테스트 조합에서 쓰는, 전송하지 않는 구현. */
object NoOpAmplitudeClient : AmplitudeClient {
    override fun track(
        eventName: String,
        properties: Map<String, String>,
    ) = Unit
}
