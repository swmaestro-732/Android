package com.chillsam.courmy.common.data.network

import com.chillsam.courmy.common.data.BuildConfig
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/**
 * 우리 API 의 호스트.
 *
 * "이 요청이 우리 서버로 가는가"를 여러 인터셉터가 각자 물어본다 — Authorization 을 붙일지
 * ([com.chillsam.courmy.common.data.di.NetworkModule]), 앱 버전 헤더를 실을지
 * ([AppVersionInterceptor]), 426 을 강제 업데이트로 볼지([UpdateRequiredInterceptor]).
 * 판단 기준이 흩어지면 한 곳만 고쳐 놓고 나머지에서 토큰이나 앱 버전이 외부 호스트로 새기 쉬워서
 * 한 군데에 둔다.
 *
 * base URL 이 비어 있거나 잘못된 형식이면 null 이고, 그때는 어떤 요청도 "우리 API"로 보지 않는다.
 */
internal val apiHost: String? = BuildConfig.API_BASE_URL.toHttpUrlOrNull()?.host
