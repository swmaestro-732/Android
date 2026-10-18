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
     * 받은 링크가 앱으로 열리려면(App Links) 세 조건이 필요하다. 2026-10-11 실측 기준
     * 1·2 는 충족됐고 **3 이 남아, 그 배포 전까지는 받은 링크가 앱 대신 브라우저로 열린다**. [wiki-needed]
     *
     * 1. ✅ **호스트 DNS** — App Links 호스트 `www.courmy.com` 이 해석된다(이전 NXDOMAIN 해소).
     * 2. ✅ **assetlinks 접근** — `https://www.courmy.com/.well-known/assetlinks.json` 이
     *    무인증 200 으로 열린다. apex·`dev.courmy.com` 은 여전히 401 이지만 App Links
     *    호스트는 `www` 라 무관하다.
     * 3. ❌ **서명 지문** — 서빙 중인 파일의 지문이 placeholder
     *    (`TODO_RELEASE_KEYSTORE_SHA256_FINGERPRINT`)다. `docs/assetlinks.json` 의 실제 지문
     *    2개로 교체 배포해야 검증이 통과한다. 새로 만들지 말고 그 파일을 그대로 올린다.
     *    교체 요청: https://github.com/swmaestro-732/Web/issues/13
     *
     * 지문이 둘인 이유 — 첫 번째는 Play 앱 서명 키다. 설치본은 Google 이 이 키로 다시 서명하므로
     * **항상** 필요하다. 두 번째는 우리 업로드 키로, CD 가 GitHub Release 에 업로드 키로 서명한
     * APK 를 첨부하는 **동안만** 필요하다. 그 직접 배포를 그만두면 두 번째 지문은 지운다.
     * 두 지문 모두 배포본 기준이라 debug 빌드로는 에뮬레이터에서 검증이 통과하지 않는다.
     *
     * 3번 배포가 끝나 영구히 켜진 것이 확정되면 이 스위치와 주석을 함께 지운다.
     */
    const val SHARE_ENABLED = true
}
