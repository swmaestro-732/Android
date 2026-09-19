package com.chillsam.courmy.course.data.courseReviews

import android.util.Log
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewItemDTO
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListData
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.RatingCountDTO
import com.chillsam.courmy.course.data.courseReviews.dto.ReviewItemAuthorDTO
import com.chillsam.courmy.course.data.courseReviews.dto.ReviewItemTagDTO
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 후기 전체보기 예시 응답 — **디버그 빌드 전용**.
 *
 * `GET /service/v1/courses/{courseId}/reviews` 는 이제 백엔드에 구현돼 있다. 다만 dev 서버 DB 에
 * 코스·리뷰가 거의 없어 레이아웃을 눈으로 확인하기 어려워서, **요청이 실패했을 때만** 예시로 대신한다.
 *
 * 빈 목록을 예시로 덮지 않는 것이 중요하다 — "후기 0건" 은 서버의 정상 응답이고,
 * 방금 쓴 후기가 목록에 붙는지 확인하려면 진짜 응답이 그대로 보여야 한다.
 *
 * 릴리스 변형은 `src/release` 의 no-op 을 컴파일하므로 이 예시가 릴리스 산출물에 **존재할 수 없다**.
 *
 * dev 서버에 시드 데이터가 들어오면 debug·release 양쪽 파일을 함께 지운다. [wiki-needed]
 */
internal object CourseReviewListFallback {
    private const val TAG = "CourseReviewList"

    /** 디버그 빌드에서도 예시 데이터를 보고 싶지 않으면 false 로 둔다. */
    private const val ENABLED = true

    /**
     * [fetch] 가 **실패했을 때만** [sample] 로 대신한다.
     *
     * 코루틴 취소는 오류가 아니라 정상 흐름이라 폴백으로 삼키지 않고 그대로 올려보낸다 —
     * 삼키면 화면을 벗어난 뒤에도 예시 데이터로 상태가 갱신된다.
     */
    suspend fun resolve(fetch: suspend () -> CourseReviewListEnvelope): CourseReviewListEnvelope {
        if (!ENABLED) return fetch()
        return try {
            fetch()
        } catch (e: CancellationException) {
            throw e
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception,
        ) {
            Log.w(TAG, "후기 전체보기 조회 실패 — 디버그 예시로 대체한다", e)
            sample()
        }
    }

    private fun tag(
        code: String,
        label: String,
        icon: String,
    ) = ReviewItemTagDTO(code = code, label = label, icon = icon)

    /**
     * [daysAgo] 일 전의 ISO-8601 UTC 문자열. 고정 날짜를 박아 두면 시간이 흐를수록 "3년 전" 이 되어
     * 상대 시각 표시가 맞는지 확인할 수 없어 호출 시점 기준으로 만든다.
     */
    private fun daysAgo(daysAgo: Int): String {
        val format =
            SimpleDateFormat(ISO_PATTERN, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        return format.format(Date(System.currentTimeMillis() - daysAgo * MILLIS_PER_DAY))
    }

    /** Figma FS-37 목업과 같은 문구·구성. 실제 응답 형태(리뷰 id·작성자 id·createdAt·태그 code)를 그대로 흉내 낸다. */
    private fun sample() =
        CourseReviewListEnvelope(
            code = 2000,
            data =
                CourseReviewListData(
                    averageRating = 4.8,
                    totalCount = 128,
                    photoCount = 86,
                    ratingDistribution =
                        listOf(
                            RatingCountDTO(rating = 5, count = 103),
                            RatingCountDTO(rating = 4, count = 18),
                            RatingCountDTO(rating = 3, count = 4),
                            RatingCountDTO(rating = 2, count = 2),
                            RatingCountDTO(rating = 1, count = 1),
                        ),
                    hasCompletedCourse = true,
                    nextCursor = null,
                    hasNext = false,
                    reviews =
                        listOf(
                            CourseReviewItemDTO(
                                id = 1,
                                author = ReviewItemAuthorDTO(id = 10, nickname = "소마님"),
                                rating = 5,
                                content = "비 오는 날 딱이에요. 통창 자리 순서대로 도니 동선도 완벽했어요. 웨이팅도 거의 없었음!",
                                createdAt = daysAgo(2),
                                photoUrls = listOf("", ""),
                                tags =
                                    listOf(
                                        tag("packed", "구성이 알차요", "📦"),
                                        tag("smooth", "흐름이 자연스러워요", "🌊"),
                                    ),
                            ),
                            CourseReviewItemDTO(
                                id = 2,
                                author = ReviewItemAuthorDTO(id = 11, nickname = "현우"),
                                rating = 4,
                                content = "대림창고 웨이팅이 좀 있었지만 전반적으로 만족스러운 코스! 마지막 와인바가 특히 좋았어요.",
                                createdAt = daysAgo(5),
                                tags = listOf(tag("combo", "장소 조합이 좋아요", "🌿")),
                            ),
                            CourseReviewItemDTO(
                                id = 3,
                                author = ReviewItemAuthorDTO(id = 12, nickname = "지은"),
                                rating = 5,
                                content = "데이트 코스로 완벽했어요. 사진 찍을 곳도 많고 이동 동선도 편했습니다 :)",
                                createdAt = daysAgo(7),
                                photoUrls = listOf(""),
                                tags =
                                    listOf(
                                        tag("date", "데이트 코스로 좋아요", "❤️"),
                                        tag("memorable", "기억에 남아요", "🔖"),
                                    ),
                            ),
                        ),
                ),
        )

    private const val ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000
}
