package com.chillsam.courmy.course.data.courseReviews

import android.util.Log
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewItemDTO
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListData
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.RatingCountDTO
import com.chillsam.courmy.course.data.courseReviews.dto.ReviewItemAuthorDTO
import com.chillsam.courmy.course.data.courseReviews.dto.ReviewItemTagDTO
import kotlinx.coroutines.CancellationException

/**
 * 후기 전체보기 예시 응답 — **디버그 빌드 전용**.
 *
 * `GET /service/v1/courses/{courseId}/reviews` 는 api-spec 에 "모킹" 으로 등재만 돼 있고 백엔드에
 * 구현이 없다(도메인 쪽 `GET /api/v1/courses/{id}/reviews` 는 고정 목만 준다). 그래서 실기기에서는
 * 이 화면이 항상 에러로 끝나 레이아웃을 볼 수가 없다.
 *
 * 릴리스 변형은 `src/release` 의 no-op 을 컴파일하므로 이 예시가 릴리스 산출물에 **존재할 수 없다**
 * (같은 이유로 [com.chillsam.courmy.course.data.courseDetail.CourseDetailReviewFallback] 도 소스셋으로 갈랐다).
 *
 * 서버가 이 엔드포인트를 구현하면 debug·release 양쪽 파일을 함께 지운다. [wiki-needed]
 */
internal object CourseReviewListFallback {
    private const val TAG = "CourseReviewList"

    /** 디버그 빌드에서도 예시 데이터를 보고 싶지 않으면 false 로 둔다. */
    private const val ENABLED = true

    /**
     * [fetch] 가 실패하거나 리뷰가 비어 있으면 [SAMPLE] 로 대신한다.
     *
     * 코루틴 취소는 오류가 아니라 정상 흐름이라 폴백으로 삼키지 않고 그대로 올려보낸다 —
     * 삼키면 화면을 벗어난 뒤에도 예시 데이터로 상태가 갱신된다.
     */
    suspend fun resolve(fetch: suspend () -> CourseReviewListEnvelope): CourseReviewListEnvelope {
        if (!ENABLED) return fetch()
        return try {
            fetch().takeIf { !it.data?.reviews.isNullOrEmpty() } ?: SAMPLE
        } catch (e: CancellationException) {
            throw e
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception,
        ) {
            Log.w(TAG, "후기 전체보기 조회 실패 — 디버그 예시로 대체한다", e)
            SAMPLE
        }
    }

    private fun tag(
        label: String,
        icon: String,
    ) = ReviewItemTagDTO(label = label, icon = icon)

    /** Figma FS-37 목업과 같은 문구·구성. */
    private val SAMPLE =
        CourseReviewListEnvelope(
            code = 200,
            data =
                CourseReviewListData(
                    averageRating = 4.8,
                    totalCount = 128,
                    photoCount = 86,
                    photoUrls = List(PHOTO_SLOTS) { "" },
                    ratingDistribution =
                        listOf(
                            RatingCountDTO(rating = 5, count = 103),
                            RatingCountDTO(rating = 4, count = 18),
                            RatingCountDTO(rating = 3, count = 4),
                            RatingCountDTO(rating = 2, count = 2),
                            RatingCountDTO(rating = 1, count = 1),
                        ),
                    reviews =
                        listOf(
                            CourseReviewItemDTO(
                                author = ReviewItemAuthorDTO(nickname = "소마님"),
                                rating = 5,
                                content = "비 오는 날 딱이에요. 통창 자리 순서대로 도니 동선도 완벽했어요. 웨이팅도 거의 없었음!",
                                relativeTime = "2일 전",
                                photoUrls = listOf("", ""),
                                tags = listOf(tag("커피 맛집", "☕"), tag("비 오는 날", "🌧"), tag("통창 뷰", "🪟")),
                            ),
                            CourseReviewItemDTO(
                                author = ReviewItemAuthorDTO(nickname = "현우"),
                                rating = 4,
                                content = "대림창고 웨이팅이 좀 있었지만 전반적으로 만족스러운 코스! 마지막 와인바가 특히 좋았어요.",
                                relativeTime = "5일 전",
                                tags = listOf(tag("마무리 좋음", "🍷"), tag("전시 볼거리", "🖼")),
                            ),
                            CourseReviewItemDTO(
                                author = ReviewItemAuthorDTO(nickname = "지은"),
                                rating = 5,
                                content = "데이트 코스로 완벽했어요. 사진 찍을 곳도 많고 이동 동선도 편했습니다 :)",
                                relativeTime = "1주 전",
                                photoUrls = listOf(""),
                                tags = listOf(tag("데이트", "💕"), tag("사진 명소", "📷")),
                            ),
                        ),
                ),
        )

    /** 사진 띠 칸 수. 예시에서는 URL 이 없어 회색 자리만 채운다. */
    private const val PHOTO_SLOTS = 4
}
