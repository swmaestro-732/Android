package com.chillsam.courmy.course.data.courseDetail

import com.chillsam.courmy.course.data.courseDetail.dto.CourseDetailEnvelope
import com.chillsam.courmy.course.data.courseDetail.dto.ReviewAuthorDTO
import com.chillsam.courmy.course.data.courseDetail.dto.ReviewPreviewDTO
import com.chillsam.courmy.course.data.courseDetail.dto.ReviewSummaryDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 리뷰 섹션 표시 확인용 임시 폴백 — **디버그 빌드 전용**.
 *
 * 서버는 `GET /service/v1/courses/{courseId}` 실 응답에서 `reviewSummary` 를 **항상 null 로** 내린다
 * (백엔드에 리뷰 조회 유스케이스가 아직 없다). 그래서 리뷰 섹션을 붙여도 실기기에서는 늘 빈 상태만
 * 보이고, 카드 레이아웃이 맞는지 눈으로 확인할 수가 없다.
 *
 * 서버의 `?mock=true` 를 쓰지 않는 이유 — 그 파라미터는 리뷰만이 아니라 **응답 전체**를 고정 목
 * (항상 "비 오는 날 성수 감성 카페 코스")으로 바꾼다. 어떤 코스를 열어도 같은 코스가 뜨므로
 * 화면 확인용으로 쓸 수 없다. 그래서 실 데이터는 그대로 두고 **비어 있는 리뷰 자리만** 여기서 채운다.
 *
 * `BuildConfig.DEBUG` 분기가 아니라 **소스셋으로 가른다** — 릴리스 변형은 `src/release` 의 no-op 을
 * 컴파일하므로 이 예시 문자열이 릴리스 산출물에 **존재할 수 없다**(R8 제거에 기대지 않는다).
 * 앱의 `benchmark` 빌드 타입도 `matchingFallbacks = ["release"]` 라 no-op 쪽을 쓴다.
 *
 * 백엔드가 `reviewSummary` 를 내려주기 시작하면 debug·release 양쪽 파일과
 * [CourseDetailDataSource] 의 한 줄, 그리고 폴백 테스트를 함께 지운다. [wiki-needed]
 */
internal object CourseDetailReviewFallback {
    /** 디버그 빌드에서도 가짜 리뷰를 보고 싶지 않으면 false 로 둔다. */
    private const val ENABLED = true

    /**
     * 서버가 리뷰를 안 내려줬을 때만 예시로 채운 응답을 돌려준다.
     * 서버가 값을 주면(빈 목록이라도) 그쪽이 이긴다 — 진짜 "리뷰 0개" 를 가짜로 덮지 않는다.
     */
    fun applyTo(envelope: CourseDetailEnvelope): CourseDetailEnvelope {
        if (!ENABLED) return envelope
        val data = envelope.data ?: return envelope
        if (data.reviewSummary != null) return envelope
        return envelope.copy(data = data.copy(reviewSummary = sample()))
    }

    /**
     * [daysAgo] 일 전의 ISO-8601 UTC 문자열. 서버가 `createdAt` 으로 주는 형식과 같아야
     * 상대 시각 변환이 실제와 같은 경로를 탄다. 고정 날짜를 박아 두면 시간이 흐를수록
     * "3년 전" 이 되어 표시가 맞는지 확인할 수 없다.
     */
    private fun daysAgo(daysAgo: Int): String {
        val format =
            SimpleDateFormat(ISO_PATTERN, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        return format.format(Date(System.currentTimeMillis() - daysAgo * MILLIS_PER_DAY))
    }

    /** Figma FS-11 목업과 같은 문구·구성. 실제 응답 형태(리뷰 id·작성자 id·createdAt)를 그대로 흉내 낸다. */
    private fun sample() =
        ReviewSummaryDTO(
            averageRating = 4.8,
            totalCount = 128,
            previews =
                listOf(
                    ReviewPreviewDTO(
                        id = 1,
                        author = ReviewAuthorDTO(id = 10, nickname = "소마님", profileImageUrl = ""),
                        rating = 5,
                        content = "비 오는 날 딱이에요. 통창 자리 순서대로 도니 동선도 완벽했어요. 웨이팅도 거의 없었어요 🌧",
                        createdAt = daysAgo(2),
                        photoUrls = emptyList(),
                    ),
                    ReviewPreviewDTO(
                        id = 2,
                        author = ReviewAuthorDTO(id = 11, nickname = "현우님", profileImageUrl = ""),
                        rating = 4,
                        content = "데이트 코스로 완벽했어요. 마지막 와인바가 특히 좋았습니다. 이동 동선도 편했어요.",
                        createdAt = daysAgo(5),
                        photoUrls = emptyList(),
                    ),
                ),
        )

    private const val ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000
}
