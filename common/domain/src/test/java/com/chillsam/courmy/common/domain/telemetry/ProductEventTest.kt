package com.chillsam.courmy.common.domain.telemetry

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ProductEventTest {
    private val telemetry = mockk<Telemetry>(relaxed = true)

    @Test
    fun `빈 검색어는 이벤트로 전송하지 않는다`() {
        telemetry.searchSubmit("", "course", "keyboard")
        telemetry.searchSubmit("  \n\t", "place", "recent")

        verify(exactly = 0) { telemetry.logEvent(any(), any()) }
    }

    @Test
    fun `검색어는 공백을 제거하고 백 자로 제한하면서 대상과 제출 경로를 보존한다`() {
        val query = "성수".repeat(60)
        telemetry.searchSubmit("  $query  ", "place", "place_suggestion")

        verify(exactly = 1) {
            telemetry.logEvent(
                ProductEvent.SearchSubmit,
                mapOf(
                    "screen" to "/search",
                    "query" to query.take(100),
                    "target" to "place",
                    "source" to "place_suggestion",
                ),
            )
        }
    }

    @Test
    fun `버튼 공통 속성은 호출자가 전달한 속성으로 덮어쓰지 않는다`() {
        telemetry.buttonClick("/login", "social_login", mapOf("screen" to "wrong", "provider" to "naver"))

        verify(exactly = 1) {
            telemetry.logEvent(
                ProductEvent.ButtonClick,
                mapOf("screen" to "/login", "button" to "social_login", "provider" to "naver"),
            )
        }
    }
}
