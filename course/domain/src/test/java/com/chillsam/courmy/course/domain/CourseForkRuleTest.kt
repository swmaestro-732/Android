package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseForkRule
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 포크 원본 장소 유지 규칙이 서버(`Course.requiredKeptPlaceCount`)와 같은 값을 내는지 고정한다.
 * 여기가 어긋나면 화면은 삭제를 허용해 놓고 저장에서 400 FORK_PLACES_NOT_KEPT 를 맞는다.
 */
class CourseForkRuleTest {
    @Test
    fun `원본이 4곳 이하면 전부 유지해야 한다`() {
        assertEquals(0, CourseForkRule.requiredKeptPlaceCount(0))
        assertEquals(2, CourseForkRule.requiredKeptPlaceCount(2))
        assertEquals(3, CourseForkRule.requiredKeptPlaceCount(3))
        assertEquals(4, CourseForkRule.requiredKeptPlaceCount(4))
    }

    @Test
    fun `원본이 5곳 이상이면 절반 이상을 올림해 유지해야 한다`() {
        assertEquals(3, CourseForkRule.requiredKeptPlaceCount(5))
        assertEquals(3, CourseForkRule.requiredKeptPlaceCount(6))
        assertEquals(4, CourseForkRule.requiredKeptPlaceCount(7))
        assertEquals(4, CourseForkRule.requiredKeptPlaceCount(8))
        assertEquals(5, CourseForkRule.requiredKeptPlaceCount(9))
        assertEquals(5, CourseForkRule.requiredKeptPlaceCount(10))
    }
}
