package com.chillsam.courmy.common.data.network

import com.chillsam.courmy.common.domain.appUpdate.AppUpdateEventBus
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 426 Upgrade Required 를 잡아 [AppUpdateEventBus] 로 알린다.
 *
 * DataSource 마다 처리하지 않고 인터셉터에 둔다 — 강제 업데이트는 어떤 화면의 어떤 요청에서
 * 내려와도 똑같이 앱 전체를 막아야 하고, 응답을 그대로 흘려보내므로 각 화면의 기존 에러 처리
 * ([com.chillsam.courmy.common.domain.error.HttpResponseException]) 는 건드리지 않는다.
 *
 * [apiHost] 로 우리 API 응답만 본다. 카카오·네이버·S3 프리사인 같은 서드파티가 자기 사정으로 426 을
 * 내려도 그건 "우리 앱이 낡았다"는 뜻이 아니다.
 *
 * 본문은 읽지 않는다 — 서버가 실어 보내는 문구는 고정 문자열이라 쓰지 않기로 했고
 * ([AppUpdateEventBus] 참고), 안 읽으면 뒤따르는 로깅·에러 파싱과 본문을 두고 다툴 일도 없다.
 *
 * 상태 코드만 보고 판단한다(에러 코드 4260 을 확인하지 않는다). 426 은 이 용도로만 쓰이고,
 * 봉투 스키마가 바뀌어도 강제 업데이트가 조용히 안 걸리는 쪽으로 깨지지 않는 편이 낫다.
 */
class UpdateRequiredInterceptor(
    private val apiHost: String?,
    private val appUpdateEventBus: AppUpdateEventBus,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == UPGRADE_REQUIRED && response.request.url.host == apiHost) {
            appUpdateEventBus.notifyUpdateRequired()
        }
        return response
    }

    private companion object {
        /** RFC 7231 426 Upgrade Required. 서버가 정한 최소 지원 빌드보다 앱이 낮다. */
        const val UPGRADE_REQUIRED = 426
    }
}
