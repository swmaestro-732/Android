package com.chillsam.courmy.course.data.courseLike.dto

import com.chillsam.courmy.course.entity.CourseLikeVO
import kotlinx.serialization.Serializable

/**
 * `POST/DELETE /api/v1/courses/{courseId}/likes` 응답 엔벨로프.
 *
 * swagger(`ApiResponseCourseLikeResponse`)는 data 의 세 필드를 모두 필수로 표시하지만,
 * 계약상 DTO 는 전 필드 nullable 로 받는다 — 서버 표기가 틀려도 파싱 실패로 쓰기 성공을
 * 실패로 둔갑시키지 않기 위해서다.
 */
@Serializable
data class CourseLikeEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseLikeDTO? = null,
)

@Serializable
data class CourseLikeDTO(
    val courseId: Long? = null,
    val liked: Boolean? = null,
    val likesCnt: Int? = null,
)

/**
 * 응답 → 서버 확정 상태.
 * [requestedLiked] 는 서버가 `liked` 를 빠뜨렸을 때의 폴백 — 2xx 를 받았으므로 요청한 상태가
 * 반영된 것으로 본다(팔로우 토글과 같은 판단). 집계 수가 없으면 보정할 수 없어 null 로 둔다.
 */
fun CourseLikeDTO.toVO(requestedLiked: Boolean): CourseLikeVO? =
    likesCnt?.let { count ->
        CourseLikeVO(
            liked = liked ?: requestedLiked,
            likeCount = count.coerceAtLeast(0),
        )
    }
