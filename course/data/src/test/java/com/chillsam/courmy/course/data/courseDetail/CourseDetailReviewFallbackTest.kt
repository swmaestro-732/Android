package com.chillsam.courmy.course.data.courseDetail

import com.chillsam.courmy.course.data.courseDetail.dto.CourseDetailEnvelope
import com.chillsam.courmy.course.data.courseDetail.dto.CourseScreenDTO
import com.chillsam.courmy.course.data.courseDetail.dto.CourseScreenData
import com.chillsam.courmy.course.data.courseDetail.dto.ReviewPreviewDTO
import com.chillsam.courmy.course.data.courseDetail.dto.ReviewSummaryDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * [CourseDetailReviewFallback] 검증 — 서버 값이 폴백보다 항상 우선한다.
 *
 * 단위 테스트는 debug 변형만 돌아서(`:course:data:test` = `testDebugUnitTest`) 여기서 보는 구현은
 * `src/debug` 쪽이다. 이 클래스가 지키려는 계약은 "언제 채우는가" 가 아니라 **"언제 채우지 않는가"** 다.
 * 백엔드가 리뷰를 내려주기 시작하면 이 파일도 함께 지운다.
 */
class CourseDetailReviewFallbackTest {
    @Test
    fun `서버가 리뷰를 안 주면 폴백으로 채운다`() {
        val envelope = CourseDetailEnvelope(data = CourseScreenData(course = CourseScreenDTO()))

        val filled = CourseDetailReviewFallback.applyTo(envelope)

        assertEquals(
            2,
            filled.data
                ?.reviewSummary
                ?.previews
                ?.size,
        )
    }

    @Test
    fun `서버가 리뷰를 주면 폴백이 덮지 않는다`() {
        val real = ReviewSummaryDTO(averageRating = 3.0, totalCount = 1, previews = emptyList())
        val envelope = CourseDetailEnvelope(data = CourseScreenData(course = CourseScreenDTO(), reviewSummary = real))

        val result = CourseDetailReviewFallback.applyTo(envelope)

        assertSame(real, result.data?.reviewSummary)
    }

    /** 리뷰가 진짜로 0개인 것과 서버가 아직 안 주는 것은 다르다 — 빈 목록도 서버 값이므로 덮지 않는다. */
    @Test
    fun `서버가 빈 리뷰 목록을 주면 그대로 둔다`() {
        val empty = ReviewSummaryDTO(averageRating = 0.0, totalCount = 0, previews = emptyList())
        val envelope = CourseDetailEnvelope(data = CourseScreenData(course = CourseScreenDTO(), reviewSummary = empty))

        val result = CourseDetailReviewFallback.applyTo(envelope)

        assertEquals(emptyList<ReviewPreviewDTO>(), result.data?.reviewSummary?.previews)
    }

    @Test
    fun `data 가 없으면 그대로 돌려준다`() {
        val envelope = CourseDetailEnvelope(code = 500, message = "서버 오류", data = null)

        val result = CourseDetailReviewFallback.applyTo(envelope)

        assertNull(result.data)
    }
}
