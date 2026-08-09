package com.chillsam.courmy.course.data.courseManage.dto

import com.chillsam.courmy.course.entity.CourseEditPlaceVO
import com.chillsam.courmy.course.entity.CourseEditVO
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * [CourseEditVO.toUpdateRequest] 매핑 검증.
 *
 * 서버는 편집 요청의 `places` 를 통째로 치환하므로, 편집 대상이 아닌 값도 불러온 그대로 되돌려
 * 보내야 한다. 빠뜨리면 사진·도보 시간이 조용히 지워진다.
 */
class UpdateCourseDtoMappingTest {
    @Test
    fun `편집 대상이 아닌 도보 분을 그대로 되돌려 보낸다`() {
        val request =
            edit(
                place(placeId = 1, walkingMinutes = 6),
                place(placeId = 2, walkingMinutes = null),
            ).toUpdateRequest()

        assertEquals(listOf(6, null), request.places.map { it.walkingMinutes })
    }

    @Test
    fun `편집 대상이 아닌 사진을 그대로 되돌려 보낸다`() {
        val request = edit(place(placeId = 1, imageUrls = listOf("a.jpg", "b.jpg"))).toUpdateRequest()

        assertEquals(listOf("a.jpg", "b.jpg"), request.places.single().imageUrls)
    }

    /**
     * 작성자가 단 해시태그를 그대로 보내야 한다. 화면이 서버 파생 카테고리(themes)를 이 자리에
     * 넣으면 사용자 태그가 영구 소실된다.
     */
    @Test
    fun `태그는 편집 값 그대로 보낸다`() {
        val request = edit(place(placeId = 1)).copy(tags = listOf("감성카페", "비오는날")).toUpdateRequest()

        assertEquals(listOf("감성카페", "비오는날"), request.tags)
    }

    /** 태그를 전부 지운 경우에도 필드가 누락되면 서버가 "변경 없음" 으로 읽어 안 지워진다. */
    @Test
    fun `태그를 전부 지우면 빈 목록을 보낸다`() {
        val request = edit(place(placeId = 1)).copy(tags = emptyList()).toUpdateRequest()

        assertEquals(emptyList<String>(), request.tags)
    }

    @Test
    fun `한마디는 공백을 다듬고 비면 null 로 보낸다`() {
        val request = edit(place(placeId = 1, tip = "  좋아요  "), place(placeId = 2, tip = "   ")).toUpdateRequest()

        assertEquals(listOf("좋아요", null), request.places.map { it.caption })
    }

    private fun edit(vararg places: CourseEditPlaceVO) =
        CourseEditVO(title = "코스", description = "소개", places = places.toList())

    private fun place(
        placeId: Long,
        tip: String = "",
        imageUrls: List<String> = emptyList(),
        walkingMinutes: Int? = null,
    ) = CourseEditPlaceVO(
        placeId = placeId,
        orderNo = (placeId - 1).toInt(),
        name = "장소 $placeId",
        tip = tip,
        imageUrls = imageUrls,
        walkingMinutes = walkingMinutes,
    )
}
