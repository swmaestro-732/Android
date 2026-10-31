package com.chillsam.courmy.main.data.auth

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.common.data.push.DeviceRegistration
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.main.data.auth.dto.LogoutRequest
import com.chillsam.courmy.main.data.auth.dto.SignupRequest
import com.chillsam.courmy.main.data.auth.dto.SocialLoginRequest
import com.chillsam.courmy.main.domain.auth.AuthRepository
import com.chillsam.courmy.main.entity.auth.SignupProfile
import com.chillsam.courmy.main.entity.auth.SocialLoginResult
import com.chillsam.courmy.main.entity.auth.SocialProvider

/**
 * 인증 저장소 구현. 응답 토큰을 [TokenStore] 에 보관하고, 신규 여부만 도메인에 돌려준다.
 * 토큰(access/refresh/registration)은 밖으로 노출하지 않는다.
 */
class AuthRepositoryImpl(
    private val dataSource: AuthDataSource,
    private val tokenStore: TokenStore,
    private val devices: DeviceRegistration,
) : AuthRepository {
    override suspend fun socialLogin(
        provider: SocialProvider,
        idToken: String,
    ): SocialLoginResult {
        // 네이버만 OIDC 미지원이라 accessToken 필드로 보낸다. 서버는 provider 에 맞는 필드
        // 하나만 허용한다(SocialLoginRequest 주석 참고).
        val request =
            if (provider == SocialProvider.NAVER) {
                SocialLoginRequest(provider = provider.name, accessToken = idToken)
            } else {
                SocialLoginRequest(provider = provider.name, idToken = idToken)
            }
        val data =
            requireNotNull(
                dataSource.socialLogin(request).data,
            ) { "social-login 응답에 data 가 없습니다." }

        if (data.newUser) {
            // 신규: 가입 화면에서 쓸 registrationToken 보관.
            tokenStore.updateRegistrationToken(
                requireNotNull(data.registrationToken) { "신규 회원 응답에 registrationToken 이 없습니다." },
            )
        } else {
            // 기존: 세션 토큰 보관 = 로그인 완료.
            tokenStore.updateSession(
                accessToken = requireNotNull(data.accessToken) { "로그인 응답에 accessToken 이 없습니다." },
                refreshToken = data.refreshToken.orEmpty(),
            )
        }
        if (!data.newUser) devices.onSessionAvailable()
        return SocialLoginResult(newUser = data.newUser)
    }

    override suspend fun signup(profile: SignupProfile) {
        val registrationToken =
            requireNotNull(tokenStore.registrationToken) { "가입에 필요한 registrationToken 이 없습니다(소셜 로그인 선행 필요)." }
        val data =
            requireNotNull(
                dataSource
                    .signup(
                        SignupRequest(
                            registrationToken = registrationToken,
                            nickname = profile.nickname,
                            handle = profile.handle,
                            profileImageUrl = profile.profileImageUrl,
                            areaCodes = profile.areaCodes.ifEmpty { null },
                            // 서버는 likeThemes 를 CourseCategory enum 이름(대문자)으로 검증한다 — 소문자 코드는 400.
                            likeThemes =
                                profile.likeThemes
                                    .map { requireNotNull(CourseTagVO.fromCode(it)) { "알 수 없는 관심 테마: $it" }.name }
                                    .distinct()
                                    .ifEmpty { null },
                        ),
                    ).data,
            ) { "signup 응답에 data 가 없습니다." }

        tokenStore.updateSession(
            accessToken = requireNotNull(data.accessToken) { "가입 응답에 accessToken 이 없습니다." },
            refreshToken = data.refreshToken.orEmpty(),
        )
        devices.onSessionAvailable()
    }

    override suspend fun restoreSession() {
        tokenStore.load()
        devices.sync()
    }

    override val isLoggedIn: Boolean
        get() = tokenStore.isLoggedIn

    override suspend fun logout() {
        try {
            devices.unregister()
            // 서버 세션 무효화는 best-effort. 로컬 세션 정리는 취소에도 보장한다.
            tokenStore.refreshToken?.takeIf { it.isNotBlank() }?.let { refresh ->
                runCatching { dataSource.logout(LogoutRequest(refresh)) }
            }
        } finally {
            tokenStore.clear()
        }
    }

    override suspend fun withdraw() {
        // 탈퇴 API 실패 시에는 로그인 상태를 유지한다.
        dataSource.withdraw()
        try {
            devices.unregister(removeRemote = false)
        } finally {
            tokenStore.clear()
        }
    }

    override suspend fun isHandleAvailable(handle: String): Boolean = dataSource.checkHandleAvailability(handle)
}
