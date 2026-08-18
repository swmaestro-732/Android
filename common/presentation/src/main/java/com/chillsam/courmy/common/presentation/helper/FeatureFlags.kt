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
     *
     * 배포할 assetlinks.json 내용은 `docs/assetlinks.json` 에 준비돼 있다. 새로 만들지 말고
     * 그 파일을 그대로 `/.well-known/` 에 올린다.
     *
     * 지문이 둘인 이유 — 첫 번째는 Play 앱 서명 키다. 설치본은 Google 이 이 키로 다시 서명하므로
     * **항상** 필요하다. 두 번째는 우리 업로드 키로, CD 가 GitHub Release 에 업로드 키로 서명한
     * APK 를 첨부하는 **동안만** 필요하다. 그 직접 배포를 그만두면 두 번째 지문은 지운다 —
     * 남겨 두면 더 이상 배포하지 않는 서명에까지 링크 검증을 열어 두는 셈이다.
     */
    const val SHARE_ENABLED = false
}
