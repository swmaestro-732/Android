package com.chillsam.courmy.common.domain.telemetry

import kotlinx.coroutines.CancellationException

/**
 * 핵심 플로우 한 건을 감싸 성공·실패를 기록한다.
 *
 * UseCase 마다 try/catch 를 늘어놓으면 도메인 로직이 기록 코드에 묻히므로 여기 한 곳에 모은다.
 * 예외는 **삼키지 않고 그대로 다시 던진다** — 기록은 관찰일 뿐 흐름을 바꾸면 안 된다.
 *
 * ```
 * suspend operator fun invoke(id: Long) = telemetry.track(AppFlow.CourseDetailLoad) {
 *     repository.getCourseDetail(id)
 * }
 * ```
 *
 * Throwable 을 통째로 잡는 것은 의도한 동작이다. 예외 종류를 좁히면 좁힌 만큼이
 * 기록 없이 조용히 지나가고, "안 돌아가는 원인을 본다"는 목적을 무너뜨린다.
 * 잡은 예외는 그대로 다시 던지므로 흐름은 바뀌지 않는다.
 */
@Suppress("TooGenericExceptionCaught")
suspend fun <T> Telemetry.track(
    flow: AppFlow,
    block: suspend () -> T,
): T {
    try {
        val result = block()
        logFlow(flow, FlowResult.Success)
        return result
    } catch (e: CancellationException) {
        // 화면 이탈 등으로 코루틴이 취소된 것은 실패가 아니다. 기록하면 실패율이 왜곡된다.
        throw e
    } catch (e: Throwable) {
        val kind = e.failureKind()
        logFlow(flow, FlowResult.Failure, detail = kind)
        recordFailure(AppFailure(area = flow.eventName, kind = kind, cause = e))
        throw e
    }
}

/**
 * 예외를 집계 가능한 짧은 분류값으로 바꾼다.
 *
 * 예외 메시지를 그대로 쓰면 서버 응답 본문이 섞여 들어오고 값 종류가 무한히 늘어나 집계가 안 된다.
 * 타입 이름만 쓰면 그 둘을 모두 피할 수 있다.
 */
private fun Throwable.failureKind(): String = javaClass.simpleName.ifBlank { "UnknownError" }
