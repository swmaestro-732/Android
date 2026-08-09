package com.chillsam.courmy.common.presentation.helper

/**
 * 아직 켤 수 없는 기능의 노출 스위치.
 *
 * 코드를 지우지 않고 화면에서만 감춘다 — 조건이 갖춰지면 값만 되돌리면 된다.
 * 조건이 확정돼 영구히 켜지면 그 스위치와 이 주석을 함께 지운다.
 */
object FeatureFlags {
    /**
     * 공유 버튼 노출 여부.
     *
     * 링크 복사·딥링크 라우팅은 동작하지만, 매니페스트의 App Links 호스트
     * (`www.courmy.com`)가 DNS 에 없고 `/.well-known/assetlinks.json` 도 배포되지 않아
     * **다른 기기에서 링크를 눌러도 앱이 열리지 않는다**(브라우저로 빠진다).
     *
     * 호스트를 확정하고 assetlinks.json 을 무인증으로 공개하면 true 로 되돌린다. [wiki-needed]
     */
    const val SHARE_ENABLED = false
}
