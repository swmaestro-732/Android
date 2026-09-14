package com.chillsam.courmy.course.data.courseReviews.dto

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * [CourseReviewListData.toVO] 매핑 검증 — 분포 비율·사진 "+N"·태그 정리.
 */
class CourseReviewListDtoMappingTest {
    @Test
    fun `집계와 리뷰를 화면 표시용 VO 로 포맷팅한다`() {
        val vo = fullData().toVO()

        assertEquals("4.8", vo.averageRatingText)
        assertEquals(5, vo.averageStars)
        assertEquals("128개", vo.totalCountText)
        assertEquals("86", vo.photoCountText)
        assertEquals(3, vo.reviews.size)
        assertEquals("소마님", vo.reviews.first().author)
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
                ).toVO()

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
                .toVO()

        assertEquals(0f, vo.distribution.single().ratio, 0.0001f)
    }

    /** 사진 띠는 4칸이고 마지막 칸이 "+N" 으로 덮이므로, 접히는 개수는 전체 - 3 이다. */
    @Test
    fun `사진이 넘치면 마지막 칸에 남은 장수를 접는다`() {
        val vo = fullData().copy(photoCount = 86, photoUrls = List(10) { "u$it" }).toVO()

        assertEquals(4, vo.photoUrls.size)
        assertEquals("+83", vo.morePhotoCountText)
    }

    /** 4칸을 다 못 채우면 접을 게 없다. "+0" 같은 배지가 뜨면 안 된다. */
    @Test
    fun `사진이 적으면 더보기 배지를 만들지 않는다`() {
        val vo = fullData().copy(photoCount = 2, photoUrls = listOf("a", "b")).toVO()

        assertEquals(2, vo.photoUrls.size)
        assertEquals("", vo.morePhotoCountText)
    }

    @Test
    fun `누락 필드는 기본값으로 안전하게 매핑된다`() {
        val vo = CourseReviewListData().toVO()

        assertEquals("0.0", vo.averageRatingText)
        assertEquals(0, vo.averageStars)
        assertEquals("0개", vo.totalCountText)
        assertEquals("", vo.photoCountText)
        assertEquals("", vo.morePhotoCountText)
        assertEquals(emptyList<Any>(), vo.distribution)
        assertEquals(emptyList<Any>(), vo.reviews)
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
                                    ReviewItemTagDTO(label = "커피 맛집", icon = "☕"),
                                    ReviewItemTagDTO(label = "  ", icon = "🌧"),
                                    ReviewItemTagDTO(label = null, icon = "🪟"),
                                ),
                        ),
                    ),
            ).toVO()

        val review = vo.reviews.single()
        assertEquals("익명", review.author)
        assertEquals(listOf("커피 맛집"), review.tags.map { it.label })
    }

    private fun fullData() =
        CourseReviewListData(
            averageRating = 4.8,
            totalCount = 128,
            photoCount = 86,
            photoUrls = listOf("a", "b", "c", "d"),
            ratingDistribution =
                listOf(
                    RatingCountDTO(rating = 3, count = 4),
                    RatingCountDTO(rating = 5, count = 103),
                    RatingCountDTO(rating = 4, count = 18),
                ),
            reviews =
                listOf(
                    CourseReviewItemDTO(
                        author = ReviewItemAuthorDTO(nickname = "소마님"),
                        rating = 5,
                        content = "동선이 완벽했어요.",
                        relativeTime = "2일 전",
                    ),
                    CourseReviewItemDTO(author = ReviewItemAuthorDTO(nickname = "현우"), rating = 4),
                    CourseReviewItemDTO(author = ReviewItemAuthorDTO(nickname = "지은"), rating = 5),
                ),
        )
}
