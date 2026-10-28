package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.main.data.saved.dto.SavedCourseScreenDTO
import com.chillsam.courmy.main.data.saved.dto.toScreenVO
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SavedCourseScreenMappingTest {
    @Test
    fun `필터 개수와 카드 메타데이터를 응답 그대로 보존한다`() {
        val result =
            Json
                .decodeFromString<SavedCourseScreenDTO>(
                    """{
              "totalCount":12,"uncompletedCount":8,"completedCount":4,
              "folders":[{"id":3,"name":"데이트","count":5}],
              "hasNext":true,"nextCursor":null,
              "savedCourses":[{"courseId":9,"completed":true,"completedAt":"2026-10-05T12:00:00",
                "isMine":true,"course":{"title":"산책","durationText":"약 2시간","placeCount":3}}]
            }""",
                ).toScreenVO()
        assertEquals(12, result.totalCount)
        assertEquals(8, result.uncompletedCount)
        assertEquals(4, result.completedCount)
        assertEquals(5, result.folders.single().count)
        assertEquals(
            "약 2시간",
            result.page.items
                .single()
                .durationText,
        )
        assertEquals(
            "2026-10-05",
            result.page.items
                .single()
                .completedAt,
        )
        assertEquals(
            true,
            result.page.items
                .single()
                .isMine,
        )
        assertFalse(result.page.hasNext)
    }
}
