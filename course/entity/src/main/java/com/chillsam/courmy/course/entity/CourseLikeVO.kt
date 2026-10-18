package com.chillsam.courmy.course.entity

/**
 * 좋아요 토글 후 서버가 확정한 상태.
 *
 * 화면은 낙관적으로 먼저 토글하므로, 이 값은 성공 후 **서버 집계로 보정**하는 용도다
 * (여러 기기·다른 사용자의 좋아요가 섞이면 로컬 ±1 계산과 어긋날 수 있다).
 */
data class CourseLikeVO(
    val liked: Boolean = false,
    val likeCount: Int = 0,
)
