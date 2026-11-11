package com.chillsam.courmy.common.domain.telemetry

enum class ProductEvent(
    val eventName: String,
) {
    Navigation("navigation"),
    ButtonClick("button_click"),
    SearchSubmit("search_submit"),
    LoginProviderResult("login_provider_result"),
    SelectionChange("selection_change"),

    /** 콘텐츠 상세 조회. 어떤 콘텐츠(코스 등)를 봤는지 식별자와 함께 남긴다(조회 집계). */
    ContentView("content_view"),

    /** 커미 AI 추천 채팅 진입. AI 채팅 사용 시작을 집계한다. */
    AiChatOpen("ai_chat_open"),

    /** 커미 AI 추천 채팅 메시지 전송. 전송 1건당 1회 — AI 채팅 사용량(사용 수) 집계에 쓴다. */
    AiChatMessageSent("ai_chat_message_sent"),
}

fun Telemetry.buttonClick(
    screen: String,
    button: String,
    properties: Map<String, String> = emptyMap(),
) {
    logEvent(ProductEvent.ButtonClick, properties + mapOf("screen" to screen, "button" to button))
}

/** 제출 시점만 기록한다. 검색 입력 중의 키 입력은 수집하지 않는다. */
fun Telemetry.searchSubmit(
    query: String,
    target: String,
    source: String,
) {
    if (query.isBlank()) return
    logEvent(
        ProductEvent.SearchSubmit,
        mapOf("screen" to "/search", "query" to query.trim().take(100), "target" to target, "source" to source),
    )
}

fun Telemetry.selectionChange(
    screen: String,
    control: String,
    value: String?,
    selected: Boolean? = null,
) {
    logEvent(
        ProductEvent.SelectionChange,
        buildMap {
            put("screen", screen)
            put("control", control)
            put("value", value ?: "all")
            selected?.let { put("selected", it.toString()) }
        },
    )
}

/**
 * 콘텐츠 상세 조회를 기록한다. [contentType] 은 `course` 같은 종류이고, 식별자는 `<type>_id`(예: `course_id`)로 담는다.
 * 양수만 기록한다(아직 안 정해진 0/음수 id 는 집계하지 않는다).
 */
fun Telemetry.contentView(
    contentType: String,
    id: Long,
    screen: String,
    extra: Map<String, String> = emptyMap(),
) {
    if (id <= 0L) return
    logEvent(
        ProductEvent.ContentView,
        extra +
            mapOf(
                "screen" to screen,
                "content_type" to contentType,
                "${contentType}_id" to id.toString(),
            ),
    )
}

/** 커미 AI 채팅 진입. [source] 는 진입 경로(예: `home_card`, `restart`). */
fun Telemetry.aiChatOpen(source: String) {
    logEvent(ProductEvent.AiChatOpen, mapOf("screen" to "/aichat", "source" to source))
}

/**
 * 커미 AI 채팅 메시지 전송. 전송 1건당 1회 호출해 사용 수를 집계한다.
 * [answerType] 은 `chip`·`text` 등 전송 종류, [step] 은 서버 대화 단계(있으면).
 */
fun Telemetry.aiChatMessageSent(
    answerType: String,
    step: String? = null,
) {
    logEvent(
        ProductEvent.AiChatMessageSent,
        buildMap {
            put("screen", "/aichat")
            put("answer_type", answerType)
            step?.let { put("step", it) }
        },
    )
}
