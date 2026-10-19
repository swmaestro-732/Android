package com.chillsam.courmy.main.data.search.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseSearchPageMappingTest {
    @Test
    fun `검색 항목과 다음 페이지 커서를 함께 전달한다`() {
        val page =
            CourseSearchDTO(
                courses = listOf(CourseSearchItemDTO(id = 42, title = "성수 산책", likesCnt = 1200)),
                nextCursor = "cursor+/=",
                hasNext = true,
            ).toPageVO()

        assertEquals("42", page.items.single().id)
        assertEquals("성수 산책", page.items.single().title)
        assertEquals("1.2k", page.items.single().likeCountText)
        assertEquals("cursor+/=", page.nextCursor)
        assertTrue(page.hasNext)
    }

    @Test
    fun `다음 커서가 없으면 hasNext가 true여도 추가 조회하지 않는다`() {
        listOf(null, "", " \t").forEach { cursor ->
            val page = CourseSearchDTO(nextCursor = cursor, hasNext = true).toPageVO()

            assertNull(page.nextCursor)
            assertFalse(page.hasNext)
            assertTrue(page.items.isEmpty())
        }
    }

    @Test
    fun `마지막 페이지는 커서 값이 남아 있어도 추가 조회하지 않는다`() {
        assertFalse(CourseSearchDTO(nextCursor = "last", hasNext = false).toPageVO().hasNext)
    }
}
