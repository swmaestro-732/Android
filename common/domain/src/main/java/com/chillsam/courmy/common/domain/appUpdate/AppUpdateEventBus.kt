package com.chillsam.courmy.common.domain.appUpdate

import kotlinx.coroutines.flow.StateFlow

/**
 * 서버가 이 앱 버전을 더 이상 받지 않을 때(HTTP 426 Upgrade Required) 알린다.
 *
 * data 레이어(응답 인터셉터)가 [notifyUpdateRequired] 로 방출하고 presentation 이 [updateRequired] 를
 * 관찰해 전체 화면 업데이트 안내로 앱을 덮는다.
 *
 * [com.chillsam.courmy.common.domain.session.SessionEventBus] 와 달리 SharedFlow 가 아니라
 * **StateFlow(sticky)** 다 — 세션 만료는 "지금 로그인 화면으로 보내라"는 일회성 신호지만 강제 업데이트는
 * 되돌릴 방법이 스토어 갱신뿐인 상태값이라, 신호가 뜬 뒤에 붙는 관찰자도 그 값을 봐야 한다.
 */
interface AppUpdateEventBus {
    val updateRequired: StateFlow<AppUpdateRequired?>

    /** [message] 는 서버가 내려준 안내 문구. 비어 있으면 화면 기본 문구를 쓴다. */
    fun notifyUpdateRequired(message: String?)
}

/**
 * 강제 업데이트 상태.
 *
 * [message] 는 서버 주도 문구다(api-design.md "에러 메시지는 서버 주도"). 네이티브 앱은 재배포가
 * 느려서, 안내 문구를 바꾸려고 앱을 다시 심지 않아도 되게 서버 문구를 그대로 띄운다.
 */
data class AppUpdateRequired(
    val message: String?,
)
