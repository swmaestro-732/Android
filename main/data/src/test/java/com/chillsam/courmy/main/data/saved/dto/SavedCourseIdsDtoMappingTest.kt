package com.chillsam.courmy.main.data.saved.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 저장 레코드 목록 → 코스 id 집합 변환 검증.
 *
 * 응답의 `id`(저장 레코드)와 `courseId`(코스)가 다르므로, 홈 카드 대조에는 courseId 만 써야 한다.
 */
class SavedCourseIdsDtoMappingTest {
    @Test
    fun `저장 레코드 id 가 아니라 코스 id 를 모은다`() {
        val ids =
            SavedCourseIdsDTO(
                savedCourses =
                    listOf(
                        SavedCourseIdItemDTO(id = 100L, courseId = 7L),
                        SavedCourseIdItemDTO(id = 101L, courseId = 9L),
                    ),
            ).toCourseIdSet()

        assertEquals(setOf("7", "9"), ids)
    }

    @Test
    fun `courseId 가 없는 항목은 건너뛴다`() {
        val ids =
            SavedCourseIdsDTO(
                savedCourses =
                    listOf(
                        SavedCourseIdItemDTO(id = 1L, courseId = null),
                        SavedCourseIdItemDTO(courseId = 3L),
                    ),
            ).toCourseIdSet()

        assertEquals(setOf("3"), ids)
    }

    @Test
    fun `savedCourses 가 없으면 빈 집합이다`() {
        assertTrue(SavedCourseIdsDTO(savedCourses = null).toCourseIdSet().isEmpty())
    }
}
