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
     * 링크 복사 자체는 동작하지만 **받은 사람이 링크를 눌러도 앱이 아니라 브라우저로 열린다**.
     * 켜 두면 열리지 않는 링크를 내보내게 되므로 감춰 둔다. [wiki-needed]
     *
     * 세 가지가 모두 갖춰져야 true 로 되돌릴 수 있다. 2026-10-02 확인 기준으로 셋 다 미충족이다.
     *
     * 1. **호스트가 DNS 에 있어야 한다.** 매니페스트의 App Links 호스트는 `www.courmy.com`
     *    인데 이 이름만 NXDOMAIN 이다. apex(`courmy.com`)와 `dev.courmy.com` 은 떠 있으므로,
     *    `www` 레코드를 만들거나 호스트를 apex 로 옮기는 두 길이 있다. 호스트를 바꾼다면
     *    [com.chillsam.courmy.main.domain.deeplink.AppLink.HOST] 와 매니페스트를 **같이** 고친다.
     * 2. **`/.well-known/assetlinks.json` 이 무인증으로 열려야 한다.** 지금은 apex 도 `dev` 도
     *    401 이다. 검증기는 토큰 없이 받아가므로 401 이면 호스트를 바꿔도 검증은 실패한다.
     *    공유 호스트를 API 서버(`dev.courmy.com`)에 묶지 않는다 — 링크는 사람 사이를 건너가는데
     *    환경마다 호스트가 다르면 서로 다른 빌드를 쓰는 사람끼리 링크가 통하지 않는다.
     * 3. **서명 지문이 등록돼 있어야 한다.** 아래 두 지문은 Play 배포본 기준이라, debug 빌드
     *    (debug 키스토어)로는 에뮬레이터에서 검증이 통과하지 않는다. debug 로 흐름을 확인하려면
     *    그 지문을 따로 넣어야 한다.
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
