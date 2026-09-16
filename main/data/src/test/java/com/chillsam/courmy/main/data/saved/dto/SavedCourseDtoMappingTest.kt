package com.chillsam.courmy.main.data.saved.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `GET /service/v1/my/saved-courses` 응답 → [com.chillsam.courmy.main.entity.saved.SavedCourseVO] 변환 검증.
 *
 * 특히 카드의 id 가 저장 레코드 id 가 아니라 **코스 id** 여야 한다(상세 이동·저장 취소가 코스 id 기준).
 */
class SavedCourseDtoMappingTest {
    private fun item(
        recordId: Long? = 10L,
        courseId: Long? = 7L,
        area: String? = "성수",
        theme: String? = "DATE",
        placeCount: Int? = 4,
        handle: String? = "jiho_routes",
        nickname: String? = "지호",
    ) = SavedCourseItemDTO(
        id = recordId,
        courseId = courseId,
        course =
            SavedCourseSummaryDTO(
                title = "비 오는 날 성수 코스",
                coverImageUrl = "https://cdn.example.com/1.jpg",
                area = area,
                theme = theme,
                placeCount = placeCount,
                author = SavedCourseAuthorDTO(handle = handle, nickname = nickname),
            ),
    )

    @Test
    fun `카드 id 는 저장 레코드가 아니라 코스 id 다`() {
        val vo = SavedCourseScreenDTO(savedCourses = listOf(item(recordId = 10L, courseId = 7L))).toVOList().single()

        assertEquals("7", vo.id)
    }

    @Test
    fun `지역과 테마를 가운뎃점으로 잇는다`() {
        val vo = SavedCourseScreenDTO(savedCourses = listOf(item())).toVOList().single()

        assertEquals("성수 · 데이트", vo.tagLabel)
        assertEquals("장소 4곳", vo.placeLabel)
        assertEquals("jiho_routes", vo.authorHandle)
    }

    @Test
    fun `지역이나 테마 한쪽이 없으면 남은 하나만 쓴다`() {
        val noArea = SavedCourseScreenDTO(savedCourses = listOf(item(area = null))).toVOList().single()
        val noTheme = SavedCourseScreenDTO(savedCourses = listOf(item(theme = null))).toVOList().single()

        assertEquals("데이트", noArea.tagLabel)
        assertEquals("성수", noTheme.tagLabel)
    }

    @Test
    fun `지역과 테마가 모두 없으면 칩이 비어 렌더되지 않는다`() {
        val vo = SavedCourseScreenDTO(savedCourses = listOf(item(area = null, theme = null))).toVOList().single()

        assertTrue(vo.tagLabel.isEmpty())
    }

    /** 핸들 미설정 작성자는 닉네임으로 대신 표기해 카드에 빈 줄을 남기지 않는다. */
    @Test
    fun `핸들이 없으면 닉네임을 쓴다`() {
        val vo = SavedCourseScreenDTO(savedCourses = listOf(item(handle = null))).toVOList().single()

        assertEquals("지호", vo.authorHandle)
    }

    @Test
    fun `courseId 가 없는 항목은 제외한다`() {
        val list =
            SavedCourseScreenDTO(
                savedCourses = listOf(item(courseId = null), item(courseId = 2L)),
            ).toVOList()

        assertEquals(1, list.size)
        assertEquals("2", list.single().id)
    }

    @Test
    fun `savedCourses 가 없으면 빈 목록이다`() {
        assertTrue(SavedCourseScreenDTO(savedCourses = null).toVOList().isEmpty())
    }
}
