package com.chillsam.courmy.common.domain.telemetry

enum class ProductEvent(
    val eventName: String,
) {
    Navigation("navigation"),
    ButtonClick("button_click"),
    SearchSubmit("search_submit"),
    LoginProviderResult("login_provider_result"),
    SelectionChange("selection_change"),
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
