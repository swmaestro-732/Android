package com.chillsam.courmy.main.domain.auth

import com.chillsam.courmy.main.entity.auth.SignupProfile
import com.chillsam.courmy.main.entity.auth.SocialLoginResult
import com.chillsam.courmy.main.entity.auth.SocialProvider

/**
 * 인증 저장소. 소셜 로그인·회원가입과 세션 토큰 보관을 담당한다.
 * 토큰(access/refresh/registration) 자체는 구현체(data)가 보관하고 밖으로 노출하지 않는다.
 */
interface AuthRepository {
    /**
     * 소셜 idToken 으로 로그인한다.
     * 기존 회원이면 access/refresh 토큰을, 신규 회원이면 registrationToken 을 내부에 저장하고
     * [SocialLoginResult.newUser] 로 분기 정보를 돌려준다.
     */
    suspend fun socialLogin(
        provider: SocialProvider,
        idToken: String,
    ): SocialLoginResult

    /** 저장된 registrationToken + 입력값으로 회원가입을 완료하고 세션 토큰을 저장한다. */
    suspend fun signup(profile: SignupProfile)

    /** 저장된 세션을 복원한다(콜드 스타트 자동 로그인). 이후 [isLoggedIn] 에 반영된다. */
    suspend fun restoreSession()

    /** 세션 토큰 보유 여부(= 로그인 상태). */
    val isLoggedIn: Boolean

    /** 로그아웃. 서버 세션을 무효화(best-effort)하고 로컬 토큰을 제거한다. */
    suspend fun logout()

    /** 회원 탈퇴. 서버 계정(Bearer 토큰으로 식별)을 삭제하고 로컬 토큰을 제거한다. */
    suspend fun withdraw()
}
