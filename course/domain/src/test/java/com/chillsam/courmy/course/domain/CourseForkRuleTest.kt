package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseForkRule
import org.junit.Assert.assertEquals
import org.junit.Test

/** 사용자 확정 정책: 복제 코스는 원본에서 서로 다른 장소 2곳을 유지한다. */
class CourseForkRuleTest {
    @Test
    fun `원본 장소 정보가 없으면 유지 개수는 0이다`() {
        assertEquals(0, CourseForkRule.requiredKeptPlaceCount(0))
    }

    @Test
    fun `원본 크기와 관계없이 서로 다른 2곳을 유지해야 한다`() {
        (1..10).forEach { originCount ->
            assertEquals("원본 ${originCount}곳", 2, CourseForkRule.requiredKeptPlaceCount(originCount))
        }
    }
}
