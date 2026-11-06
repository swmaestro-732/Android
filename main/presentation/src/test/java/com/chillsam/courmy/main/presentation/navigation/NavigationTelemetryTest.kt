package com.chillsam.courmy.main.presentation.navigation

import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.ProductEvent
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class NavigationTelemetryTest {
    private val telemetry = mockk<Telemetry>(relaxed = true)
    private val events = mutableListOf<Pair<ProductEvent, Map<String, String>>>()

    @Before
    fun captureEvents() {
        every { telemetry.logEvent(any(), any()) } answers {
            events += firstArg<ProductEvent>() to secondArg<Map<String, String>>()
        }
    }

    @Test
    fun `같은 엔트리와 목적지 없는 이동은 기록하지 않는다`() {
        val key = GenericNavKey("/course/detail", mapOf("courseId" to "169"))

        telemetry.recordNavigation(key, key.copy(), "push", NavigationSource("/course/detail", "course"))
        telemetry.recordNavigation(key, null, "back")
        telemetry.recordNavigation(null, null, "push")

        verify(exactly = 0) { telemetry.logEvent(any(), any()) }
    }

    @Test
    fun `같은 경로에서 다른 코스로 이동하면 이전과 다음 코스 식별자를 기록한다`() {
        telemetry.recordNavigation(
            from = GenericNavKey("/course/detail", mapOf("courseId" to "169")),
            to = GenericNavKey("/course/detail", mapOf("courseId" to "170")),
            action = "push",
        )

        assertEquals(
            ProductEvent.Navigation to
                mapOf(
                    "from_screen" to "/course/detail",
                    "to_screen" to "/course/detail",
                    "action" to "push",
                    "from_courseId" to "169",
                    "to_courseId" to "170",
                ),
            events.single(),
        )
    }

    @Test
    fun `검색 입력을 pop한 후에도 실제 버튼을 누른 검색 화면을 별도로 보존한다`() {
        telemetry.recordNavigation(
            from = GenericNavKey("/home"),
            to = GenericNavKey("/search/result", mapOf("query" to "성수")),
            action = "push",
            source = NavigationSource("/search", "submit_keyboard"),
        )

        assertEquals(
            mapOf(
                "from_screen" to "/home",
                "to_screen" to "/search/result",
                "action" to "push",
                "source_screen" to "/search",
                "trigger" to "button",
                "button" to "submit_keyboard",
            ),
            events.single().second,
        )
    }

    @Test
    fun `이동 속성은 허용된 식별자만 포함하고 검색어와 프로필과 좌표를 제외한다`() {
        val publicArgs =
            mapOf(
                "courseId" to "169",
                "placeId" to "21",
                "planId" to "32",
                "theme" to "date",
                "tab" to "plans",
                "target" to "place",
                "section" to "nearby",
                "sido" to "11",
            )
        val privateArgs =
            mapOf(
                "query" to "개인 검색어",
                "handle" to "private_handle",
                "latitude" to "37.51",
                "longitude" to "127.02",
                "targetLatitude" to "37.52",
                "targetLongitude" to "127.03",
                "bounds" to "private_bounds",
                "token" to "secret_token",
            )
        telemetry.recordNavigation(
            GenericNavKey("/map", publicArgs + privateArgs),
            GenericNavKey("/map/search", publicArgs + privateArgs),
            "push",
        )

        val expected =
            mapOf("from_screen" to "/map", "to_screen" to "/map/search", "action" to "push") +
                publicArgs.mapKeys { "from_${it.key}" } + publicArgs.mapKeys { "to_${it.key}" }
        assertEquals(expected, events.single().second)
    }

    @Test
    fun `비공개 인자만 달라져도 실제 화면 이동은 기록하되 인자는 보내지 않는다`() {
        telemetry.recordNavigation(
            GenericNavKey("/search/result", mapOf("query" to "성수")),
            GenericNavKey("/search/result", mapOf("query" to "홍대")),
            "push",
        )

        assertEquals(
            mapOf("from_screen" to "/search/result", "to_screen" to "/search/result", "action" to "push"),
            events.single().second,
        )
    }

    @Test
    fun `비동기 완료 이동은 버튼 클릭 대신 완료 사유로 기록한다`() {
        telemetry.recordNavigation(
            GenericNavKey("/course/create"),
            GenericNavKey("/course/complete"),
            "push",
            NavigationSource("/course/create", "save_success", trigger = "result"),
        )

        val properties = events.single().second
        assertEquals("result", properties["trigger"])
        assertEquals("save_success", properties["reason"])
        assertFalse(properties.containsKey("button"))
    }

    @Test
    fun `첫 화면은 앱 시작을 출발점으로 기록한다`() {
        telemetry.recordNavigation(null, GenericNavKey("/home"), "replace")

        assertEquals("app_start", events.single().second["from_screen"])
        assertFalse(events.single().second.containsKey("source_screen"))
    }
}
