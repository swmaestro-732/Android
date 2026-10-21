package com.chillsam.courmy.course.data.placeReviews

import android.util.Log
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewItemDTO
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListData
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.RatingCountDTO
import com.chillsam.courmy.course.data.courseReviews.dto.ReviewItemAuthorDTO
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 장소 후기 예시 응답 — **디버그 빌드 전용**.
 *
 * dev 서버 DB 에 장소 리뷰가 거의 없어 레이아웃을 눈으로 확인하기 어려우므로,
 * **요청이 실패했을 때만** 예시로 대신한다. 빈 목록(후기 0건)은 정상 응답이라 덮지 않는다
 * ([com.chillsam.courmy.course.data.courseReviews.CourseReviewListFallback] 과 같은 규칙).
 *
 * 릴리스 변형은 `src/release` 의 no-op 을 컴파일하므로 이 예시가 릴리스 산출물에 **존재할 수 없다**.
 *
 * dev 서버에 시드 데이터가 들어오면 debug·release 양쪽 파일을 함께 지운다. [wiki-needed]
 */
internal object PlaceReviewListFallback {
    private const val TAG = "PlaceReviewList"

    /** 디버그 빌드에서도 예시 데이터를 보고 싶지 않으면 false 로 둔다. */
    private const val ENABLED = true

    /** [fetch] 가 **실패했을 때만** [sample] 로 대신한다. 코루틴 취소는 폴백으로 삼키지 않는다. */
    suspend fun resolve(fetch: suspend () -> CourseReviewListEnvelope): CourseReviewListEnvelope {
        if (!ENABLED) return fetch()
        return try {
            fetch()
        } catch (e: CancellationException) {
            throw e
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception,
        ) {
            Log.w(TAG, "장소 후기 조회 실패 — 디버그 예시로 대체한다", e)
            sample()
        }
    }

    /** [daysAgo] 일 전의 ISO-8601 UTC 문자열. 호출 시점 기준이라 상대 시각 표시를 확인할 수 있다. */
    private fun daysAgo(daysAgo: Int): String {
        val format =
            SimpleDateFormat(ISO_PATTERN, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        return format.format(Date(System.currentTimeMillis() - daysAgo * MILLIS_PER_DAY))
    }

    /** Figma FS-11 장소 시트 목업과 같은 문구·구성. */
    private fun sample() =
        CourseReviewListEnvelope(
            code = 2000,
            data =
                CourseReviewListData(
                    averageRating = 4.8,
                    totalCount = 128,
                    ratingDistribution =
                        listOf(
                            RatingCountDTO(rating = 5, count = 103),
                            RatingCountDTO(rating = 4, count = 18),
                            RatingCountDTO(rating = 3, count = 7),
                        ),
                    nextCursor = null,
                    hasNext = false,
                    reviews =
                        listOf(
                            CourseReviewItemDTO(
                                id = 1,
                                author = ReviewItemAuthorDTO(id = 10, nickname = "현우님"),
                                rating = 5,
                                content = "팡도르가 정말 맛있어요. 통창 자리 뷰도 최고. 웨이팅은 조금 있었어요.",
                                createdAt = daysAgo(3),
                            ),
                            CourseReviewItemDTO(
                                id = 2,
                                author = ReviewItemAuthorDTO(id = 11, nickname = "소마님"),
                                rating = 4,
                                content = "분위기 좋고 커피도 괜찮았어요. 주말엔 자리 잡기가 쉽지 않네요.",
                                createdAt = daysAgo(6),
                            ),
                        ),
                ),
        )

    private const val ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000
}
