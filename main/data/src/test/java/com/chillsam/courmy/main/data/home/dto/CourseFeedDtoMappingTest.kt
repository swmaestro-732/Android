package com.chillsam.courmy.main.data.home.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `GET /service/v1/courses` 응답 → [com.chillsam.courmy.main.entity.home.HomeCourseVO] 변환 검증.
 *
 * 서버가 nullable 로 내려주는 필드(theme·coverImageUrl·savesCnt)와, 화면이 쓸 수 없는 항목(id 없음)을
 * 어떻게 처리하는지 골든 케이스로 고정한다.
 */
class CourseFeedDtoMappingTest {
    @Test
    fun `모든 필드가 채워지면 그대로 옮긴다`() {
        val vo =
            CourseFeedDTO(
                courses =
                    listOf(
                        CourseFeedItemDTO(
                            id = 7L,
                            title = "비 오는 날 성수 감성 카페 코스",
                            coverImageUrl = "https://cdn.example.com/1.jpg",
                            theme = "CAFETOUR",
                            savesCnt = 342,
                        ),
                    ),
            ).toVOList()
                .single()

        assertEquals("7", vo.id)
        assertEquals("비 오는 날 성수 감성 카페 코스", vo.title)
        assertEquals("https://cdn.example.com/1.jpg", vo.coverImageUrl)
        assertEquals("카페투어", vo.categoryLabel)
        assertEquals("342", vo.saveCountText)
    }

    /** 저장 수는 프로필 통계와 같은 포맷(1000 → "1k")을 쓴다. */
    @Test
    fun `저장 수가 1000 이상이면 축약한다`() {
        val vo = CourseFeedDTO(courses = listOf(CourseFeedItemDTO(id = 1L, savesCnt = 1400))).toVOList().single()

        assertEquals("1.4k", vo.saveCountText)
    }

    @Test
    fun `id 가 없는 항목은 상세로 이동할 수 없어 제외한다`() {
        val list =
            CourseFeedDTO(
                courses =
                    listOf(
                        CourseFeedItemDTO(id = null, title = "id 없음"),
                        CourseFeedItemDTO(id = 2L, title = "정상"),
                    ),
            ).toVOList()

        assertEquals(1, list.size)
        assertEquals("2", list.single().id)
    }

    /** 미선택(draft 유래) 코스는 theme 이 null 이고, 모르는 카테고리는 칩을 비워 렌더하지 않는다. */
    @Test
    fun `theme 이 null 이거나 모르는 값이면 카테고리 라벨이 빈다`() {
        val nullTheme = CourseFeedDTO(courses = listOf(CourseFeedItemDTO(id = 1L, theme = null))).toVOList().single()
        val unknown = CourseFeedDTO(courses = listOf(CourseFeedItemDTO(id = 1L, theme = "NEW_ONE"))).toVOList().single()

        assertTrue(nullTheme.categoryLabel.isEmpty())
        assertTrue(unknown.categoryLabel.isEmpty())
    }

    @Test
    fun `courses 가 없으면 빈 목록이다`() {
        assertTrue(CourseFeedDTO(courses = null).toVOList().isEmpty())
    }

    @Test
    fun `누락된 문자열 필드는 빈 문자열로 채운다`() {
        val vo = CourseFeedDTO(courses = listOf(CourseFeedItemDTO(id = 3L))).toVOList().single()

        assertEquals("", vo.title)
        assertEquals("", vo.coverImageUrl)
        assertEquals("0", vo.saveCountText)
    }
}
