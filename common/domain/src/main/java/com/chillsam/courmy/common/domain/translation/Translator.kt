package com.chillsam.courmy.common.domain.translation

/**
 * 한국어 텍스트를 다른 언어로 번역하는 온디바이스 번역기(구현은 data 레이어).
 *
 * presentation 은 이 인터페이스만 알고 번역 SDK 를 직접 호출하지 않는다.
 * 구현이 바뀌어도(온디바이스 ↔ 클라우드) 화면은 그대로 둘 수 있게 한다.
 */
interface Translator {
    /**
     * [text] 를 [target] 언어로 번역해 돌려준다.
     *
     * - [target] 이 [AppLanguage.KOREAN] 이거나 [text] 가 비어 있으면 원문을 그대로 반환한다.
     * - 모델(언어팩) 준비가 필요하면 내부에서 처리한다. 네트워크·모델 오류 시 예외를 던진다.
     */
    suspend fun translate(
        text: String,
        target: AppLanguage,
    ): String
}
