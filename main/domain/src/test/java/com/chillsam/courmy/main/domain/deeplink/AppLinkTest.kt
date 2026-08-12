package com.chillsam.courmy.main.domain.deeplink

import com.chillsam.courmy.common.domain.navigation.NavRoute
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * [AppLink.of] 검증 — 공유 링크가 deep-link 로 되돌아올 수 있는 형태여야 한다.
 */
class AppLinkTest {
    @Test
    fun `인자가 없으면 path 만 붙인다`() {
        assertEquals("https://www.courmy.com/my", AppLink.of(NavRoute("/my")))
    }

    @Test
    fun `인자는 query 로 붙인다`() {
        val url = AppLink.of(NavRoute("/course/detail", mapOf("courseId" to "7")))

        assertEquals("https://www.courmy.com/course/detail?courseId=7", url)
    }

    /** 핸들·제목에 공백이나 한글이 섞여도 링크가 중간에서 끊기면 안 된다. */
    @Test
    fun `인자 값을 퍼센트 인코딩한다`() {
        val url = AppLink.of(NavRoute("/user/profile", mapOf("handle" to "허 나영")))

        assertEquals("https://www.courmy.com/user/profile?handle=%ED%97%88+%EB%82%98%EC%98%81", url)
    }

    @Test
    fun `인자가 여럿이면 앰퍼샌드로 잇는다`() {
        val url = AppLink.of(NavRoute("/course/detail", linkedMapOf("courseId" to "7", "tab" to "map")))

        assertEquals("https://www.courmy.com/course/detail?courseId=7&tab=map", url)
    }
}
