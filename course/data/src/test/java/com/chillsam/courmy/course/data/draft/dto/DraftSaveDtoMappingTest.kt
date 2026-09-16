package com.chillsam.courmy.course.data.draft.dto

import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.entity.CourseVisibility
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [CourseDraftVO.toDraftUpdateRequest] 매핑 검증 — 이미 만들어 둔 초안을 다시 저장할 때 쓰는 `PATCH` 본문.
 *
 * 갱신은 생성과 같은 규칙을 따라야 한다. 갈라지면 임시저장을 한 번 더 눌렀을 뿐인데 도보 시간이나
 * 장소 순서가 달라진다.
 */
class DraftSaveDtoMappingTest {
    @Test
    fun `임시저장 갱신은 발행하지 않는다`() {
        val request = draft(place("1"), place("2")).toDraftUpdateRequest()

        assertFalse(request.isPublished)
    }

    @Test
    fun `순번을 0 부터 다시 매기고 마지막 장소의 도보 분은 비운다`() {
        val request =
            draft(
                place("7", walkingMinutes = 6),
                place("3", walkingMinutes = 9),
            ).toDraftUpdateRequest()

        assertEquals(listOf(7L, 3L), request.places.map { it.placeId })
        assertEquals(listOf(0, 1), request.places.map { it.orderNo })
        assertEquals(listOf(6, null), request.places.map { it.walkingMinutes })
    }

    /**
     * 서버 `imageUrls` 는 기본값 없는 non-null 이라 키가 빠지면 400 이 난다. 사진을 한 장도 고르지
     * 않은 장소(임시저장은 0장 허용)에서 정확히 이 경우가 생기므로 빈 배열이라도 실려 나가야 한다.
     */
    @Test
    fun `사진이 없어도 imageUrls 키는 남는다`() {
        val request = draft(place("1"), place("2")).toDraftUpdateRequest()

        val encoded = Json.encodeToString(request)
        assertTrue(encoded.contains("\"imageUrls\":[]"))
    }

    @Test
    fun `빈 커버와 빈 설명은 지움으로 보낸다`() {
        val request = draft(place("1"), place("2")).toDraftUpdateRequest()

        assertNull(request.thumbnailUrl)
        assertNull(request.description)
    }

    @Test
    fun `공개 범위와 태그는 초안 값 그대로 보낸다`() {
        val request =
            draft(place("1"), place("2"))
                .copy(tags = listOf("성수", "카페"), visibility = CourseVisibility.PRIVATE)
                .toDraftUpdateRequest()

        assertEquals(listOf("성수", "카페"), request.tags)
        assertEquals("PRIVATE", request.visibility)
    }

    private fun draft(vararg places: CoursePlaceVO) = CourseDraftVO(name = "성수 코스", places = places.toList())

    private fun place(
        id: String,
        walkingMinutes: Int? = null,
    ) = CoursePlaceVO(id = id, name = "장소 $id", walkingMinutes = walkingMinutes)
}
