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
 * - [photoUrls]         상단 사진 띠에 그릴 URL. 화면이 보여줄 만큼만 잘라서 담는다.
 * - [morePhotoCountText] 사진 띠 마지막 칸의 "+82". 더 없으면 빈 문자열.
 * - [reviews]           리뷰 목록. **서버가 준 순서 그대로**이며 그게 "최신순"이다.
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
) {
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
