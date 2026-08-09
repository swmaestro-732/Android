package com.chillsam.courmy.course.data.courseCreate.dto

import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * [CourseDraftVO.toCreateRequest] 매핑 검증.
 *
 * 서버는 도보 시간을 스스로 계산하지 않고 이 요청에 실린 `walkingMinutes` 를 그대로 저장한다.
 * 값을 빼고 보내면 저장된 코스가 영구히 "도보 0분" 이 되므로 계약을 테스트로 고정한다.
 */
class CreateCourseDtoMappingTest {
    @Test
    fun `장소별 도보 분을 실어 보내고 마지막 장소는 비운다`() {
        val request =
            draft(
                place("1", walkingMinutes = 6),
                place("2", walkingMinutes = 9),
                place("3"),
            ).toCreateRequest()

        assertEquals(listOf(6, 9, null), request.places.map { it.walkingMinutes })
    }

    /**
     * 도보 분은 "이 장소 → 다음 장소" 구간 값이라, 중간 장소가 빠지면 마지막 장소에 남은 값이
     * 존재하지 않는 구간을 가리키게 된다. 마지막은 항상 비운다.
     */
    @Test
    fun `id 가 숫자가 아닌 장소를 걸러도 마지막 장소의 도보 분은 비운다`() {
        val request = draft(place("1", walkingMinutes = 6), place("stub", walkingMinutes = 9)).toCreateRequest()

        assertEquals(listOf(1L), request.places.map { it.placeId })
        assertNull(request.places.single().walkingMinutes)
    }

    @Test
    fun `걸어갈 수 없는 구간은 서버 약속값 그대로 보낸다`() {
        val request =
            draft(
                place("1", walkingMinutes = CoursePlaceVO.UNREACHABLE_ON_FOOT),
                place("2"),
            ).toCreateRequest()

        assertEquals(CoursePlaceVO.UNREACHABLE_ON_FOOT, request.places[0].walkingMinutes)
    }

    @Test
    fun `순번은 사용자가 담은 순서대로 0 부터 다시 매긴다`() {
        val request = draft(place("7"), place("3"), place("5")).toCreateRequest()

        assertEquals(listOf(0, 1, 2), request.places.map { it.orderNo })
        assertEquals(listOf(7L, 3L, 5L), request.places.map { it.placeId })
    }

    private fun CourseDraftVO.toCreateRequest() = toCreateRequest(thumbnailUrl = null, published = true)

    private fun draft(vararg places: CoursePlaceVO) = CourseDraftVO(name = "코스", places = places.toList())

    private fun place(
        id: String,
        walkingMinutes: Int? = null,
    ) = CoursePlaceVO(id = id, name = "장소 $id", walkingMinutes = walkingMinutes)
}
