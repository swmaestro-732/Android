package com.chillsam.courmy.common.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 우리 API 요청에 앱 빌드 번호와 플랫폼을 실어 보낸다.
 * 서버는 이 값으로 최소 지원 버전을 판정해 낡은 앱에 426 을 내리고, 그건 [UpdateRequiredInterceptor] 가 받는다.
 *
 * [apiHost] 로 우리 API 에만 붙인다 — 카카오·네이버·S3 프리사인은 이 헤더를 모르고,
 * 앱 버전을 외부 호스트에 알릴 이유도 없다([com.chillsam.courmy.common.data.di.NetworkModule] 의
 * Authorization 과 같은 판단이다).
 *
 * [appBuild] 가 null 이면 두 헤더를 **모두 생략한다**. 서버 계약상 헤더가 없으면 그냥 통과지만
 * 형식이 틀리면 400(`INVALID_APP_HEADER`)이라, 값을 못 구했을 때 0 같은 가짜 값으로 채우면
 * 앱의 모든 요청이 죽는다. 버전 판정을 못 받는 쪽이 전부 실패하는 쪽보다 낫다.
 */
class AppVersionInterceptor(
    private val apiHost: String?,
    private val appBuild: Long?,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (appBuild == null || original.url.host != apiHost) return chain.proceed(original)

        return chain.proceed(
            original
                .newBuilder()
                .header(HEADER_APP_BUILD, appBuild.toString())
                .header(HEADER_APP_PLATFORM, PLATFORM_ANDROID)
                .build(),
        )
    }

    private companion object {
        const val HEADER_APP_BUILD = "X-App-Build"
        const val HEADER_APP_PLATFORM = "X-App-Platform"
        const val PLATFORM_ANDROID = "Android"
    }
}
