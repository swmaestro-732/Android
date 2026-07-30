package com.chillsam.courmy.main.data.auth.dto

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

/** POST /api/v1/auth/signup 요청. */
@Serializable
data class SignupRequest(
    val registrationToken: String,
    val nickname: String,
    val handle: String,
    val profileImageUrl: String? = null,
    val areaCodes: List<String>? = null,
    val likeTagIds: List<Long>? = null,
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
