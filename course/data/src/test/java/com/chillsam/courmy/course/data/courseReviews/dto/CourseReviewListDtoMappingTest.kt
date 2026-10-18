package com.chillsam.courmy.course.data.courseReviews.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [CourseReviewListData.toVO] 매핑 검증 — 분포 비율·사진 "+N"·태그 정리·상대 시각·내 리뷰 판정.
 */
class CourseReviewListDtoMappingTest {
    @Test
    fun `집계와 리뷰를 화면 표시용 VO 로 포맷팅한다`() {
        val vo = fullData().toVO(myUserId = null, nowMillis = NOW)

        assertEquals("4.8", vo.averageRatingText)
        assertEquals(5, vo.averageStars)
        assertEquals("128개", vo.totalCountText)
        assertEquals("86", vo.photoCountText)
        assertEquals(3, vo.reviews.size)
        assertEquals("소마님", vo.reviews.first().author)
        assertEquals(1L, vo.reviews.first().id)
    }

    /**
     * 막대 길이의 분모는 totalCount 가 아니라 **분포 합계**다.
     * 서버가 일부 구간만 내려주거나 totalCount 와 어긋나도 비율이 1을 넘지 않아야 한다.
     */
    @Test
    fun `분포 비율은 분포 합계를 분모로 쓴다`() {
        val vo =
            fullData()
                .copy(
                    totalCount = 999,
                    ratingDistribution =
                        listOf(
                            RatingCountDTO(rating = 5, count = 3),
                            RatingCountDTO(rating = 4, count = 1),
                        ),
                ).toVO(myUserId = null, nowMillis = NOW)

        assertEquals(listOf(5, 4), vo.distribution.map { it.rating })
        assertEquals(0.75f, vo.distribution[0].ratio, 0.0001f)
        assertEquals(0.25f, vo.distribution[1].ratio, 0.0001f)
    }

    /** 리뷰가 0건이면 나눗셈 분모가 0이다. 비율은 0f 로 떨어져야 하고 NaN 이면 막대가 깨진다. */
    @Test
    fun `분포 합계가 0이면 비율은 0이다`() {
        val vo =
            fullData()
                .copy(ratingDistribution = listOf(RatingCountDTO(rating = 5, count = 0)))
                .toVO(myUserId = null, nowMillis = NOW)

        assertEquals(0f, vo.distribution.single().ratio, 0.0001f)
    }

    /**
     * 서버는 사진만 모아 둔 필드를 주지 않는다. 띠는 리뷰들의 사진을 순서대로 펼쳐 만들고,
     * 4칸 중 마지막이 "+N" 으로 덮이므로 접히는 개수는 **전체 사진 수 - 3** 이다.
     */
    @Test
    fun `사진 띠는 리뷰 사진을 펼쳐 만들고 넘치면 마지막 칸에 접는다`() {
        val vo =
            CourseReviewListData(
                photoCount = 86,
                reviews =
                    listOf(
                        item(id = 1, photoUrls = listOf("a", "b", "c")),
                        item(id = 2, photoUrls = listOf("d", "e")),
                    ),
            ).toVO(myUserId = null, nowMillis = NOW)

        assertEquals(listOf("a", "b", "c", "d"), vo.photoUrls)
        assertEquals("+83", vo.morePhotoCountText)
    }

    /** 4칸을 다 못 채우면 접을 게 없다. "+0" 같은 배지가 뜨면 안 된다. */
    @Test
    fun `사진이 적으면 더보기 배지를 만들지 않는다`() {
        val vo =
            CourseReviewListData(
                photoCount = 2,
                reviews = listOf(item(id = 1, photoUrls = listOf("a", "b"))),
            ).toVO(myUserId = null, nowMillis = NOW)

        assertEquals(listOf("a", "b"), vo.photoUrls)
        assertEquals("", vo.morePhotoCountText)
    }

    @Test
    fun `누락 필드는 기본값으로 안전하게 매핑된다`() {
        val vo = CourseReviewListData().toVO(myUserId = null, nowMillis = NOW)

        assertEquals("0.0", vo.averageRatingText)
        assertEquals(0, vo.averageStars)
        assertEquals("0개", vo.totalCountText)
        assertEquals("", vo.photoCountText)
        assertEquals("", vo.morePhotoCountText)
        assertEquals(emptyList<Any>(), vo.distribution)
        assertEquals(emptyList<Any>(), vo.reviews)
        assertNull(vo.nextCursor)
        assertFalse(vo.hasNext)
    }

    /** 문구 없는 태그는 칩에 그릴 게 없다. 이모지만 남은 칩을 만들지 않는다. */
    @Test
    fun `문구가 없는 태그는 버리고 작성자 없는 리뷰는 익명으로 둔다`() {
        val vo =
            CourseReviewListData(
                reviews =
                    listOf(
                        CourseReviewItemDTO(
                            author = null,
                            rating = 4,
                            tags =
                                listOf(
                                    ReviewItemTagDTO(code = "packed", label = "구성이 알차요", icon = "📦"),
                                    ReviewItemTagDTO(code = "smooth", label = "  ", icon = "🌊"),
                                    ReviewItemTagDTO(code = "combo", label = null, icon = "🌿"),
                                ),
                        ),
                    ),
            ).toVO(myUserId = null, nowMillis = NOW)

        val review = vo.reviews.single()
        assertEquals("익명", review.author)
        assertEquals(listOf("구성이 알차요"), review.tags.map { it.label })
        // code 는 화면에 안 쓰더라도 작성 요청에 다시 실어야 해서 보존한다.
        assertEquals(listOf("packed"), review.tags.map { it.code })
    }

    /** 서버는 상대 시각 문구를 주지 않는다. `createdAt`(UTC)을 앱이 "N일 전" 으로 바꾼다. */
    @Test
    fun `createdAt 을 상대 시각 문구로 바꾼다`() {
        val vo =
            CourseReviewListData(
                reviews =
                    listOf(
                        item(id = 1, createdAt = "2026-09-17T00:00:00Z"),
                        item(id = 2, createdAt = "2026-09-18T23:30:00Z"),
                        item(id = 3, createdAt = null),
                    ),
            ).toVO(myUserId = null, nowMillis = NOW)

        assertEquals("2일 전", vo.reviews[0].dateText)
        assertEquals("30분 전", vo.reviews[1].dateText)
        // 시각을 못 읽으면 날짜 줄만 비우고 리뷰 자체는 그대로 보여준다.
        assertEquals("", vo.reviews[2].dateText)
    }

    /** 서버가 isMine 을 주지 않아 작성자 id 와 내 id 를 비교해 채운다. 삭제 버튼 노출 근거다. */
    @Test
    fun `내 id 와 작성자 id 가 같은 리뷰만 내 리뷰로 표시한다`() {
        val data =
            CourseReviewListData(
                reviews = listOf(item(id = 1, authorId = 7), item(id = 2, authorId = 9)),
            )

        val mine = data.toVO(myUserId = 7, nowMillis = NOW)
        assertTrue(mine.reviews[0].isMine)
        assertFalse(mine.reviews[1].isMine)
        assertTrue(mine.hasMyReview)

        // 비로그인은 어느 리뷰도 내 것이 아니다.
        val guest = data.toVO(myUserId = null, nowMillis = NOW)
        assertFalse(guest.reviews.any { it.isMine })
        assertFalse(guest.hasMyReview)
    }

    /**
     * 커서 없이 hasNext 만 true 로 오면 다음 요청을 만들 수 없다.
     * 그대로 믿으면 같은 페이지를 무한히 다시 받으므로 끝으로 처리한다.
     */
    @Test
    fun `커서가 없으면 hasNext 가 true 여도 끝으로 본다`() {
        val vo =
            CourseReviewListData(hasNext = true, nextCursor = null)
                .toVO(myUserId = null, nowMillis = NOW)

        assertFalse(vo.hasNext)

        val more =
            CourseReviewListData(hasNext = true, nextCursor = "c1")
                .toVO(myUserId = null, nowMillis = NOW)

        assertTrue(more.hasNext)
        assertEquals("c1", more.nextCursor)
    }

    private fun item(
        id: Long,
        authorId: Long = 1L,
        createdAt: String? = "2026-09-19T00:00:00Z",
        photoUrls: List<String> = emptyList(),
    ) = CourseReviewItemDTO(
        id = id,
        author = ReviewItemAuthorDTO(id = authorId, nickname = "닉"),
        rating = 5,
        createdAt = createdAt,
        photoUrls = photoUrls,
    )

    private fun fullData() =
        CourseReviewListData(
            averageRating = 4.8,
            totalCount = 128,
            photoCount = 86,
            ratingDistribution =
                listOf(
                    RatingCountDTO(rating = 3, count = 4),
                    RatingCountDTO(rating = 5, count = 103),
                    RatingCountDTO(rating = 4, count = 18),
                ),
            reviews =
                listOf(
                    CourseReviewItemDTO(
                        id = 1,
                        author = ReviewItemAuthorDTO(id = 10, nickname = "소마님"),
                        rating = 5,
                        content = "동선이 완벽했어요.",
                        createdAt = "2026-09-17T00:00:00Z",
                    ),
                    CourseReviewItemDTO(id = 2, author = ReviewItemAuthorDTO(id = 11, nickname = "현우"), rating = 4),
                    CourseReviewItemDTO(id = 3, author = ReviewItemAuthorDTO(id = 12, nickname = "지은"), rating = 5),
                ),
        )

    private companion object {
        /** 2026-09-19T00:00:00Z. 상대 시각 검증을 위해 "지금"을 고정한다. */
        const val NOW = 1_789_776_000_000L
    }
}
