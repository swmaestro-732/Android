package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 코스 후기 전체보기 화면(FS-37) 데이터.
 *
 * 코스 상세(FS-11)의 미리보기 2건과 달리 전체 목록 + 집계(평균·별점 분포·사진)를 함께 그린다.
 *
 * - [averageRatingText] 평균 별점 표시값 (예: "4.8")
 * - [averageStars]      헤더 별 5칸에 채울 개수. 평균을 반올림한 값이라 표시용이다.
 * - [totalCountText]    리뷰 총 개수 (예: "128개")
 * - [distribution]      별점 분포. 5점부터 내림차순이며 서버가 준 구간만 담는다.
 * - [photoCountText]    후기 사진 총 개수 (예: "86"). 사진이 없으면 빈 문자열.
 * - [photoUrls]         상단 사진 띠에 그릴 URL. 서버가 사진만 모아 주는 필드는 없어서
 *                       **받아 온 리뷰들의 사진을 순서대로 펼쳐** 화면이 보여줄 만큼만 잘라 담는다.
 * - [morePhotoCountText] 사진 띠 마지막 칸의 "+82". 더 없으면 빈 문자열.
 * - [reviews]           리뷰 목록. 서버가 정렬해서 주므로 순서를 그대로 쓴다.
 * - [nextCursor]        다음 페이지 커서(불투명 문자열). 다음 요청에 그대로 돌려준다. 끝이면 null.
 * - [hasNext]           다음 페이지 존재 여부. [nextCursor] 가 없으면 더 받지 않는다.
 * - [hasCompletedCourse] 내가 이 코스를 완주했는지. **작성 버튼을 이 값으로 막지 않는다** —
 *                       서버가 작성 시 완주를 요구하지 않고, 완주 판정 자체가 아직 고정값이다.
 */
@Serializable
data class CourseReviewListVO(
    val averageRatingText: String,
    val averageStars: Int,
    val totalCountText: String,
    val distribution: List<CourseRatingBucketVO> = emptyList(),
    val photoCountText: String = "",
    val photoUrls: List<String> = emptyList(),
    val morePhotoCountText: String = "",
    val reviews: List<CourseReviewVO> = emptyList(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val hasCompletedCourse: Boolean = false,
    val totalCount: Int? = null,
) {
    /** 서버 집계가 확인된 0~3건 장소에만 외부 리뷰 검색을 제안한다. */
    val shouldSuggestExternalReviews: Boolean
        get() = totalCount != null && totalCount in 0..3 && !hasNext

    /**
     * 받아 온 페이지 안에 내 리뷰가 있는지. 작성 버튼을 미리 감추는 데만 쓴다.
     *
     * 내 리뷰가 아직 못 받은 뒤쪽 페이지에 있으면 false 라서 작성 버튼이 보일 수 있다.
     * 그때는 서버가 409(이미 작성함)로 막고 화면이 그 사유를 안내한다 — 이 값만 믿지 않는다.
     */
    val hasMyReview: Boolean get() = reviews.any { it.isMine }

    companion object {
        val empty: CourseReviewListVO =
            CourseReviewListVO(
                averageRatingText = "0.0",
                averageStars = 0,
                totalCountText = "0개",
            )
    }
}

/**
 * 별점 분포 막대 1줄.
 *
 * - [rating] 별점 값(5~1)
 * - [ratio]  전체 대비 비율(0f~1f). 막대 길이로 쓰며, 리뷰가 0건이면 0f 다.
 */
@Serializable
data class CourseRatingBucketVO(
    val rating: Int,
    val ratio: Float,
)
