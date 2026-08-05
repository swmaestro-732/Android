package com.chillsam.courmy.main.entity.auth

/**
 * 아이디(핸들) 중복 확인 결과.
 * 길이·형식은 클라이언트에서 먼저 거르고, 통과한 값만 서버에 사용 가능 여부를 묻는다.
 * 안내 문구는 화면마다 다를 수 있어 presentation 이 정한다.
 */
enum class HandleCheckResult {
    /** 사용 가능. */
    AVAILABLE,

    /** 길이 규칙 위반. */
    INVALID_LENGTH,

    /** 허용 문자(영문 소문자·숫자·밑줄) 위반. */
    INVALID_FORMAT,

    /** 예약어이거나 이미 사용 중. */
    TAKEN,

    /** 네트워크·서버 오류로 판정하지 못함. */
    ERROR,
    ;

    val isAvailable: Boolean
        get() = this == AVAILABLE
}
