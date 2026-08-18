package com.chillsam.courmy.course.data.draft.dto

import com.chillsam.courmy.course.data.courseDetail.dto.CoursePlaceDTO
import com.chillsam.courmy.course.data.courseDetail.dto.CoursePlaceImageDTO
import com.chillsam.courmy.course.data.courseDetail.dto.CoursePlaceLocationDTO
import com.chillsam.courmy.course.data.courseDetail.dto.CourseScreenDTO
import com.chillsam.courmy.course.data.courseDetail.dto.CourseScreenData
import com.chillsam.courmy.course.entity.CourseVisibility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 저장해 둔 초안 → 작성 화면 초안 VO 매핑 검증("이어서 작성").
 *
 * 작성 화면은 장소를 서버 place id 문자열로 식별하고 좌표로 도보 시간을 다시 구한다. 둘 중 하나라도
 * 빠지면 이어서 작성한 코스가 저장될 때 장소가 통째로 사라지거나 도보 0분이 된다.
 */
class DraftContentDtoMappingTest {
    @Test
    fun `장소를 orderNo 순으로 정렬하고 place id 를 그대로 넘긴다`() {
        val draft =
            data(
                place(placeId = 2, orderNo = 1, name = "둘째"),
                place(placeId = 1, orderNo = 0, name = "첫째"),
            ).toDraftVO(CourseVisibility.PUBLIC)

        assertEquals(listOf("첫째", "둘째"), draft.places.map { it.name })
        assertEquals(listOf("1", "2"), draft.places.map { it.id })
    }

    @Test
    fun `좌표와 도보 분을 그대로 들고 온다`() {
        val draft = data(place(placeId = 1, orderNo = 0, walkToNext = 7)).toDraftVO(CourseVisibility.PUBLIC)

        assertEquals(37.5464440372535, draft.places.single().latitude!!, 0.0)
        assertEquals(127.043028992489, draft.places.single().longitude!!, 0.0)
        assertEquals(7, draft.places.single().walkingMinutes)
    }

    @Test
    fun `장소 사진은 orderNo 순으로 담는다`() {
        val draft =
            data(
                place(
                    placeId = 1,
                    orderNo = 0,
                    images =
                        listOf(
                            CoursePlaceImageDTO(imageUrl = "b.jpg", orderNo = 1),
                            CoursePlaceImageDTO(imageUrl = "a.jpg", orderNo = 0),
                        ),
                ),
            ).toDraftVO(CourseVisibility.PUBLIC)

        assertEquals(listOf("a.jpg", "b.jpg"), draft.places.single().photoUrls)
    }

    /** 공개 설정은 화면 조합 응답에 없다. 못 읽었으면 임시저장 기본값(PUBLIC)으로 둔다. */
    @Test
    fun `공개 설정을 모르면 PUBLIC 으로 둔다`() {
        val draft = data(place(placeId = 1, orderNo = 0)).toDraftVO(visibility = null)

        assertEquals(CourseVisibility.PUBLIC, draft.visibility)
    }

    /** 응답의 이미지는 사용자가 올린 코스 사진이라 장소 대표 사진 자리에 끼워 넣지 않는다. */
    @Test
    fun `장소 썸네일은 비워 둔다`() {
        val draft =
            data(
                place(placeId = 1, orderNo = 0, images = listOf(CoursePlaceImageDTO(imageUrl = "a.jpg", orderNo = 0))),
            ).toDraftVO(CourseVisibility.PUBLIC)

        assertTrue(
            draft.places
                .single()
                .thumbnailUrl
                .isEmpty(),
        )
    }

    @Test
    fun `제목과 커버를 초안으로 되돌린다`() {
        val draft = data(place(placeId = 1, orderNo = 0)).toDraftVO(CourseVisibility.PUBLIC)

        assertEquals("성수 코스", draft.name)
        assertEquals("cover.jpg", draft.thumbnailUrl)
        assertEquals(listOf("성수"), draft.tags)
    }

    private fun data(vararg places: CoursePlaceDTO) =
        CourseScreenData(
            course =
                CourseScreenDTO(
                    title = "성수 코스",
                    coverImageUrl = "cover.jpg",
                    themes = listOf("CAFE"),
                    tags = listOf("성수"),
                    description = "설명",
                    places = places.toList(),
                ),
        )

    private fun place(
        placeId: Long,
        orderNo: Int,
        name: String = "장소",
        walkToNext: Int? = null,
        images: List<CoursePlaceImageDTO> = emptyList(),
    ) = CoursePlaceDTO(
        placeId = placeId,
        orderNo = orderNo,
        name = name,
        walkingMinutesToNext = walkToNext,
        categories = listOf("CAFE"),
        location = CoursePlaceLocationDTO(latitude = 37.5464440372535, longitude = 127.043028992489),
        images = images,
    )
}
