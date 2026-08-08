package com.chillsam.courmy.common.domain.telemetry

/**
 * 출시 후 운영에서 앱 상태를 파악하기 위한 기록 계약.
 *
 * 세 가지 질문에 답하는 것이 목적이다.
 * 1. 앱이 어쩌다 터졌나 → [setScreen] 이 남긴 화면 경로가 크래시 리포트에 붙는다.
 * 2. 안 돌아가면 원인이 뭔가 → [recordFailure]
 * 3. 핵심 로직이 잘 도나 → [logFlow]
 *
 * 구현(Crashlytics/Analytics)은 data 레이어에 두고, domain·presentation 은 이 인터페이스만 안다.
 * SDK 를 갈아끼울 때 구현 한 곳만 바꾸면 되게 하기 위한 경계다.
 *
 * **여기에 넘기는 값에 개인정보·토큰·서버 응답 본문을 담지 않는다.** 리포트는 외부(Firebase)에 저장된다.
 */
interface Telemetry {
    /**
     * 현재 화면을 기록한다. 크래시가 나면 직전까지의 화면 이동 경로가 리포트에 함께 보인다.
     *
     * 이 앱은 Compose 단일 Activity 라 Analytics 의 자동 화면 추적이 화면을 구분하지 못한다.
     * 그래서 네비게이션 지점에서 직접 넣어 준다.
     */
    fun setScreen(name: String)

    /**
     * 핵심 플로우의 결과. 성공·실패를 같은 이름으로 남겨 성공률을 볼 수 있게 한다.
     *
     * @param detail 실패 사유 같은 짧은 분류값. 사용자 입력이나 서버 메시지를 그대로 넣지 않는다.
     */
    fun logFlow(
        flow: AppFlow,
        result: FlowResult,
        detail: String? = null,
    )

    /** 앱이 죽지는 않았지만 기능이 실패한 경우(non-fatal). */
    fun recordFailure(failure: AppFailure)
}

/**
 * 추적 대상 핵심 플로우.
 *
 * 이벤트 이름을 문자열로 흩어 두면 오타와 표기 흔들림으로 집계가 갈라진다.
 * 여기 한 곳에서만 정의하고, 이름은 Analytics 관례대로 snake_case 를 쓴다.
 */
enum class AppFlow(
    val eventName: String,
) {
    Login("login"),
    Signup("signup"),
    Onboarding("onboarding"),
    CourseCreate("course_create"),
    CourseComplete("course_complete"),
    CourseUpdate("course_update"),
    CourseDetailLoad("course_detail_load"),
    CourseSave("course_save"),
    FollowAuthor("follow_author"),
}

enum class FlowResult {
    Success,
    Failure,
}

/**
 * 실패 한 건. 화면에 에러를 띄우고 끝내면 운영에서는 아무것도 보이지 않으므로 여기로 함께 남긴다.
 *
 * @param area 어디서 났는지(플로우 이름 또는 `network`).
 * @param kind 무엇이 실패했는지 분류값(`http_500`, `timeout`, `parse_error` 등). 집계 키라 값 종류가 적어야 한다.
 * @param detail 경로 템플릿처럼 **식별자가 없는** 부가 정보만.
 * @param cause 스택을 남기기 위한 원인 예외. 메시지에 응답 본문이 들어 있으면 안 된다.
 */
data class AppFailure(
    val area: String,
    val kind: String,
    val detail: String? = null,
    val cause: Throwable? = null,
)

/** 기록하지 않는 구현. 테스트와 텔레메트리를 붙이지 않은 조합에서 쓴다. */
object NoOpTelemetry : Telemetry {
    override fun setScreen(name: String) = Unit

    override fun logFlow(
        flow: AppFlow,
        result: FlowResult,
        detail: String?,
    ) = Unit

    override fun recordFailure(failure: AppFailure) = Unit
}
