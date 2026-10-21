package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseReviewListVO
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExternalPlaceReviewsEligibilityTest {
    @Test
    fun `실제 후기 0건과 3건은 외부 후기를 제안한다`() {
        assertTrue(CourseReviewListVO.empty.copy(totalCount = 0).shouldSuggestExternalReviews)
        assertTrue(CourseReviewListVO.empty.copy(totalCount = 3).shouldSuggestExternalReviews)
    }

    @Test
    fun `첫 페이지가 비어 있어도 전체 4건이면 외부 후기를 제안하지 않는다`() {
        val reviews = CourseReviewListVO.empty.copy(totalCount = 4)
        assertTrue(reviews.reviews.isEmpty())
        assertFalse(reviews.shouldSuggestExternalReviews)
    }

    @Test
    fun `집계를 모르면 0개 표시 문구만으로 소수 후기를 판단하지 않는다`() {
        assertFalse(CourseReviewListVO.empty.shouldSuggestExternalReviews)
        assertFalse(CourseReviewListVO.empty.copy(totalCountText = "3개").shouldSuggestExternalReviews)
    }

    @Test
    fun `집계가 적어도 다음 페이지가 있으면 외부 후기를 제안하지 않는다`() {
        val reviews = CourseReviewListVO.empty.copy(totalCount = 3, hasNext = true, nextCursor = "next")
        assertFalse(reviews.shouldSuggestExternalReviews)
    }

    @Test
    fun `잘못된 음수 집계는 외부 후기 조건에서 제외한다`() {
        assertFalse(CourseReviewListVO.empty.copy(totalCount = -1).shouldSuggestExternalReviews)
    }
}
