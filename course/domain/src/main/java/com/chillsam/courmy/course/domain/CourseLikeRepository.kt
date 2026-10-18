package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseLikeVO

/**
 * 코스 좋아요/좋아요 취소 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 사용자는 서버가 JWT 로 식별하므로 인자에 사용자 id 를 싣지 않는다(로그인 필수).
 */
interface CourseLikeRepository {
    /**
     * [liked] 가 true 면 좋아요(POST), false 면 취소(DELETE).
     *
     * 서버가 확정 상태(좋아요 여부·집계 수)를 돌려주면 그대로 반환하고, 2xx 인데 data 가 없으면
     * null 을 돌려준다 — 성공은 맞으므로 예외로 바꾸지 않고, 보정할 값만 없다고 알린다
     * (팔로우 토글의 "2xx 면 요청한 상태가 반영된 것으로 본다"와 같은 결).
     */
    suspend fun setLiked(
        courseId: Long,
        liked: Boolean,
    ): CourseLikeVO?
}
