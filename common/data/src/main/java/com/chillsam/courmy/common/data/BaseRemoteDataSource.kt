package com.chillsam.courmy.common.data

import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.domain.error.HttpResponseStatus
import retrofit2.Response

abstract class BaseRemoteDataSource {
    /**
     * 바디 없는 성공 응답을 허용하는 검증(상태 코드만 본다).
     *
     * S3 프리사인 PUT 처럼 2xx 에 본문이 없는(또는 204 로 Retrofit 이 body 를 null 로 만드는)
     * 요청은 [checkResponse] 를 쓰면 성공인데도 null body 로 실패한다.
     */
    protected fun checkSuccess(response: Response<*>) {
        if (response.isSuccessful) return
        throw response.toHttpException()
    }

    protected fun <T> checkResponse(response: Response<T>): T {
        if (response.isSuccessful) {
            return response.body()
                ?: throw IllegalStateException("Successful response with null body: ${response.raw().request.url}")
        }
        throw response.toHttpException()
    }

    protected inline fun <T, R> checkResponse(
        response: Response<T>,
        crossinline returnValue: (T) -> R,
    ): R {
        if (response.isSuccessful) {
            val body =
                response.body()
                    ?: throw IllegalStateException("Successful response with null body: ${response.raw().request.url}")
            return returnValue(body)
        }
        throw response.toHttpException()
    }
}

/** 실패 응답을 공통 예외로 바꾼다. `inline` 함수에서도 쓰므로 public 이다. */
fun Response<*>.toHttpException(): HttpResponseException {
    val errorBody = errorBody()?.string()
    return HttpResponseException(
        status = HttpResponseStatus.create(code()),
        rawCode = code(),
        errorRequestUrl =
            raw()
                .request.url
                .toString(),
        msg = "Http Request Failed (${code()}) ${message()}, $errorBody",
        cause = errorBody?.let(::Throwable),
    )
}
