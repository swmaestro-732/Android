package com.chillsam.courmy.main.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** POST /api/v1/auth/social-login 요청. provider 는 서버 enum 문자열("KAKAO" 등)과 일치. */
@Serializable
data class SocialLoginRequest(
    val provider: String,
    val idToken: String,
)

/** POST /api/v1/auth/logout 요청. */
@Serializable
data class LogoutRequest(
    val refreshToken: String,
)

/** data 없는 공통 응답(로그아웃·탈퇴). */
@Serializable
data class SimpleEnvelope(
    val code: Int? = null,
    val message: String? = null,
)

/**
 * POST /api/v1/auth/signup 요청.
 *
 * [likeThemes] 는 코스 카테고리 이름 목록이다(`CAFETOUR`, `CULTURE` …).
 *
 * 이 엔드포인트는 모르는 필드를 400 없이 **조용히 무시**한다 — 이름이 어긋나면 200 을 받고도
 * 값만 빠지므로, 필드명은 서버 DTO 와 정확히 맞춰야 한다.
 */
@Serializable
data class SignupRequest(
    val registrationToken: String,
    val nickname: String,
    val handle: String,
    val profileImageUrl: String? = null,
    val areaCodes: List<String>? = null,
    val likeThemes: List<String>? = null,
)

/** 공통 응답 봉투 { code, message, data, fieldErrors }. */
@Serializable
data class SocialLoginEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: SocialLoginDataDTO? = null,
)

@Serializable
data class SocialLoginDataDTO(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val registrationToken: String? = null,
    // 서버는 신규 여부를 "isNewUser" 로 내려준다(도메인/레포는 newUser 로 계속 사용).
    @SerialName("isNewUser")
    val newUser: Boolean = false,
)

@Serializable
data class SignupEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: SignupDataDTO? = null,
)

@Serializable
data class SignupDataDTO(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val user: SignupUserDTO? = null,
)

@Serializable
data class SignupUserDTO(
    val id: Long? = null,
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
)

/** GET /api/v1/users/availability 응답. */
@Serializable
data class AvailabilityEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: AvailabilityDTO? = null,
)

@Serializable
data class AvailabilityDTO(
    val available: Boolean = false,
)
