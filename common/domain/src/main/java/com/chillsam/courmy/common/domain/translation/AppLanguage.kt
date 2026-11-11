package com.chillsam.courmy.common.domain.translation

/**
 * 앱 표시 언어. 온디바이스 번역의 대상 언어이자 사용자가 설정에서 고르는 값이다.
 *
 * [KOREAN] 은 원문(번역하지 않음) 기준이다. [code] 는 저장·ML Kit 매핑에 쓰는 소문자 코드다.
 */
enum class AppLanguage(
    val code: String,
) {
    KOREAN("ko"),
    ENGLISH("en"),
    JAPANESE("ja"),
    ;

    companion object {
        /** 저장된 코드를 되읽는다. 알 수 없거나 비어 있으면 원문([KOREAN])으로 떨어뜨린다. */
        fun fromCode(code: String?): AppLanguage = entries.firstOrNull { it.code == code } ?: KOREAN
    }
}
