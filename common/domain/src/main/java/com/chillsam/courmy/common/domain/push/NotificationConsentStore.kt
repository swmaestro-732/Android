package com.chillsam.courmy.common.domain.push

/**
 * 홈 최초 진입 팝업에서 받는 기기 단위 알림 수신 동의(SCRUM-756). 로그인과 무관하다.
 *
 * 서비스(인앱 활동) 알림 동의는 필수라 팝업 확인 자체가 동의이며 별도 값으로 들고 있지 않는다.
 * 광고성·야간만 서버 `devices` 테이블로 전달된다.
 */
interface NotificationConsentStore {
    /** 동의 팝업을 이미 처리(확인)했는지. true 면 다시 띄우지 않는다. */
    val isDecided: Boolean

    /** 광고성 정보 수신 동의(선택). */
    val marketingAgreed: Boolean

    /** 야간(21시~8시) 수신 동의(선택). 광고성 동의 없이는 켤 수 없다. */
    val nightAgreed: Boolean

    /**
     * 서버가 확인한 광고성 동의 처리 시각(ISO-8601 instant 문자열). null 이면 미동의/미확인.
     * 서버 응답(PUT·GET)으로만 채워지며, 동의·철회 결과를 사용자에게 안내하는 데 쓴다(SCRUM-756).
     */
    val marketingAgreedAt: String?

    /** 서버가 확인한 야간 동의 처리 시각(ISO-8601 instant 문자열). null 이면 미동의/미확인. */
    val nightAgreedAt: String?

    /** 동의 결과를 저장하고 서버 반영을 시작한다. 광고성 동의가 없으면 야간 동의는 버린다. */
    suspend fun save(
        marketingAgreed: Boolean,
        nightAgreed: Boolean,
    )

    /**
     * 서버의 현재 동의 상태를 조회해 로컬 캐시(동의 여부·처리 시각)를 맞춘다.
     * 아직 서버로 못 보낸 로컬 변경이 있으면(동기화 대기) 덮어쓰지 않고 건너뛴다.
     */
    suspend fun refreshFromServer()
}
