package com.chillsam.courmy.common.domain.appUpdate

import kotlinx.coroutines.flow.StateFlow

/**
 * 서버가 이 앱 버전을 더 이상 받지 않을 때(HTTP 426 Upgrade Required) 알린다.
 *
 * data 레이어(응답 인터셉터)가 [notifyUpdateRequired] 로 방출하고 presentation 이 [updateRequired] 를
 * 관찰해 전체 화면 업데이트 안내로 앱을 대신한다.
 *
 * [com.chillsam.courmy.common.domain.session.SessionEventBus] 와 달리 SharedFlow 가 아니라
 * **StateFlow(sticky)** 다 — 세션 만료는 "지금 로그인 화면으로 보내라"는 일회성 신호지만 강제 업데이트는
 * 되돌릴 방법이 스토어 갱신뿐인 상태값이라, 신호가 뜬 뒤에 붙는 관찰자도 그 값을 봐야 한다.
 *
 * 서버 문구를 싣지 않는다. api-design.md 는 "에러 메시지는 서버 주도"지만 426 만은 예외로 두는데,
 * 서버가 내려주는 값이 `CommonErrorCode.APP_UPDATE_REQUIRED` 의 고정 문자열("앱 업데이트가 필요합니다.")
 * 이라 원격으로 바꿀 수 있는 카피가 아니고, 그대로 띄우면 안내 화면 제목과 같은 말이 두 번 나온다.
 * 사용자에게 보일 문구는 앱이 가진다.
 */
interface AppUpdateEventBus {
    /** true 가 되면 스토어 갱신 전까지 돌아오지 않는다. */
    val updateRequired: StateFlow<Boolean>

    fun notifyUpdateRequired()
}
