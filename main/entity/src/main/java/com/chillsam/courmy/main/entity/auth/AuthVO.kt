package com.chillsam.courmy.main.entity.auth

/** 소셜 로그인 제공자. 서버 계약(social-login provider enum)의 문자열 값과 정확히 일치시킨다. */
enum class SocialProvider {
    KAKAO,
    NAVER,
    GOOGLE,
    APPLE,
}

/**
 * 소셜 로그인 결과.
 * - [newUser] true 면 회원가입이 필요(가입 화면으로), false 면 로그인 완료(홈으로).
 * 토큰(access/refresh) 또는 registrationToken 은 data 레이어가 보관하므로 여기 노출하지 않는다.
 */
data class SocialLoginResult(
    val newUser: Boolean,
)

/**
 * 신규 회원가입 입력값(가입 완료 화면에서 모아 전달).
 * - [nickname] 닉네임(최대 20자) · [handle] 핸들 · [profileImageUrl] 프로필 이미지 URL(선택)
 * - [areaCodes] 관심 지역 법정동코드 목록
 * - [likeThemes] 관심 테마. 코스 태그 12종(`CourseTagVO`)의 **소문자 코드**(`date` …)를 담는다.
 *   서버 요청은 CourseCategory enum 이름(대문자)이라 data 레이어가 전송 직전에 변환한다
 */
data class SignupProfile(
    val nickname: String,
    val handle: String,
    val profileImageUrl: String? = null,
    val areaCodes: List<String> = emptyList(),
    val likeThemes: List<String> = emptyList(),
)

/** 가입/로그인 완료 사용자 요약. */
data class AuthUser(
    val id: Long,
    val nickname: String,
    val handle: String,
    val profileImageUrl: String = "",
)
