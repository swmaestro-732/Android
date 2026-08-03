package com.chillsam.courmy.course.data.courseDetail.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * [CourseScreenData.toVO] 매핑 검증 — 표시용 문자열 포맷팅·순서 정렬·null 처리.
 */
class CourseDetailDtoMappingTest {
    @Test
    fun `course 필드를 화면 표시용 VO 로 포맷팅한다`() {
        val vo = fullData().toVO()

        assertEquals("비 오는 날 성수 감성 카페 코스", vo.title)
        assertEquals("성수 · 데이트", vo.category)
        assertEquals("지호님", vo.authorName)
        assertEquals("@jiho_routes", vo.authorHandle)
        assertEquals("4곳", vo.placeCountText)
        assertEquals("도보 20분", vo.walkText)
        assertEquals("1.2k 따라감", vo.followerText)
        assertEquals("4.3", vo.rating)
        assertEquals("6개", vo.reviewCountText)
    }

    @Test
    fun `장소는 orderNo 순으로 정렬되고 order 는 1부터, 마지막 도보 안내는 null`() {
        val vo =
            fullData()
                .copy(
                    course =
                        fullData().course!!.copy(
                            places =
                                listOf(
                                    place(orderNo = 1, name = "둘째", walkToNext = null),
                                    place(orderNo = 0, name = "첫째", walkToNext = 6),
                                ),
                        ),
                ).toVO()

        assertEquals(listOf("첫째", "둘째"), vo.places.map { it.name })
        assertEquals(listOf(1, 2), vo.places.map { it.order })
        assertEquals("도보 6분", vo.places[0].walkToNextText)
        assertNull(vo.places[1].walkToNextText)
        assertEquals("1/2", vo.places[0].photoCountText)
    }

    @Test
    fun `리뷰 작성자 null 이면 익명, photoUrls 는 사진 목록`() {
        val vo =
            fullData()
                .copy(
                    reviewSummary =
                        ReviewSummaryDTO(
                            averageRating = 5.0,
                            totalCount = 1,
                            previews =
                                listOf(
                                    ReviewPreviewDTO(
                                        author = null,
                                        rating = 5,
                                        content = "좋아요",
                                        relativeTime = "1일 전",
                                        photoUrls = listOf("a", "b"),
                                    ),
                                ),
                        ),
                ).toVO()

        assertEquals("익명", vo.reviews[0].author)
        assertEquals(listOf("a", "b"), vo.reviews[0].photoUrls)
        assertEquals("1일 전", vo.reviews[0].dateText)
    }

    @Test
    fun `이미지 URL 을 매핑한다 - 커버_작성자_장소_리뷰`() {
        val data =
            fullData().copy(
                course =
                    fullData().course!!.copy(
                        coverImageUrl = "cover.jpg",
                        author = AuthorDTO("지호님", "jiho_routes", "avatar.jpg"),
                        places =
                            listOf(
                                CoursePlaceDTO(
                                    orderNo = 0,
                                    name = "어니언",
                                    images =
                                        listOf(
                                            CoursePlaceImageDTO("p2.jpg", 1),
                                            CoursePlaceImageDTO("p1.jpg", 0),
                                        ),
                                ),
                            ),
                    ),
                reviewSummary =
                    ReviewSummaryDTO(
                        averageRating = 5.0,
                        totalCount = 1,
                        previews =
                            listOf(
                                ReviewPreviewDTO(
                                    author = ReviewAuthorDTO("성수러버", "rv.jpg"),
                                    rating = 5,
                                    content = "좋아요",
                                    relativeTime = "1일 전",
                                    photoUrls = listOf("rp.jpg"),
                                ),
                            ),
                    ),
            )
        val vo = data.toVO()

        assertEquals("cover.jpg", vo.coverImageUrl)
        assertEquals("avatar.jpg", vo.authorImageUrl)
        // orderNo 순으로 정렬되어야 한다
        assertEquals(listOf("p1.jpg", "p2.jpg"), vo.places[0].imageUrls)
        assertEquals("rv.jpg", vo.reviews[0].authorImageUrl)
        assertEquals(listOf("rp.jpg"), vo.reviews[0].photoUrls)
    }

    @Test
    fun `장소 위치의 위도와 경도를 지도 좌표로 매핑한다`() {
        val vo =
            fullData()
                .copy(
                    course =
                        fullData().course!!.copy(
                            places =
                                listOf(
                                    place(orderNo = 0, name = "어니언 성수", walkToNext = null).copy(
                                        location =
                                            CoursePlaceLocationDTO(
                                                latitude = 37.5445,
                                                longitude = 127.0575,
                                            ),
                                    ),
                                ),
                        ),
                ).toVO()

        assertEquals(37.5445, vo.places.single().latitude!!, 0.0)
        assertEquals(127.0575, vo.places.single().longitude!!, 0.0)
    }

    @Test
    fun `누락 필드는 기본값으로 안전하게 매핑된다`() {
        val vo = CourseScreenData(course = CourseScreenDTO(), reviewSummary = null).toVO()

        assertEquals("", vo.title)
        assertEquals("", vo.category)
        assertEquals("0곳", vo.placeCountText)
        assertEquals("도보 0분", vo.walkText)
        assertEquals("0 따라감", vo.followerText)
        assertEquals("0.0", vo.rating)
        assertEquals("0개", vo.reviewCountText)
        assertEquals(emptyList<Any>(), vo.places)
        assertEquals(emptyList<Any>(), vo.reviews)
    }

    private fun place(
        orderNo: Int,
        name: String,
        walkToNext: Int?,
    ) = CoursePlaceDTO(
        orderNo = orderNo,
        name = name,
        caption = "팁",
        walkingMinutesToNext = walkToNext,
        categories = listOf("카페", "베이커리"),
        images = listOf(CoursePlaceImageDTO("u1", 0), CoursePlaceImageDTO("u2", 1)),
    )

    private fun fullData() =
        CourseScreenData(
            course =
                CourseScreenDTO(
                    title = "비 오는 날 성수 감성 카페 코스",
                    themes = listOf("성수", "데이트"),
                    description = "소개",
                    stats = CourseStatsDTO(placeCount = 4, walkingMinutes = 20, tracingCountLabel = "1.2k"),
                    author = AuthorDTO(nickname = "지호님", handle = "jiho_routes"),
                    places = listOf(place(orderNo = 0, name = "어니언 성수", walkToNext = 6)),
                ),
            reviewSummary =
                ReviewSummaryDTO(
                    averageRating = 4.3,
                    totalCount = 6,
                    previews = emptyList(),
                ),
        )
}
