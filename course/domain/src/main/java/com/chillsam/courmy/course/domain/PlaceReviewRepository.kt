package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort

/**
 * 장소 리뷰 Repository.
 *
 * 코스 후기와 **데이터는 다르지만**(대상이 코스가 아니라 장소) 응답 구조는 같다 — 둘 다 BFF 가
 * 리뷰 목록에 작성자 프로필·집계를 병합해 내려주는 같은 모양이라([CourseReviewRepository] 주석의
 * 2026-08-18 이관 배경 참고) 반환 타입으로 [CourseReviewListVO] 를 그대로 쓴다.
 * 장소 전용 필드가 생겨 모양이 갈라지면 그때 VO 를 분리한다.
 */
interface PlaceReviewRepository {
    /**
     * 장소 후기 한 페이지를 조회한다(`GET /service/v1/places/{placeId}/reviews`).
     *
     * [cursor] 가 null 이면 첫 페이지다. 다음 페이지는 직전 결과의 `nextCursor` 를 그대로 넘긴다.
     */
    suspend fun getPlaceReviews(
        placeId: Long,
        sort: CourseReviewSort,
        cursor: String? = null,
        size: Int = DEFAULT_PAGE_SIZE,
    ): CourseReviewListVO

    companion object {
        /** 한 번에 받아 올 후기 수. 코스 후기와 같은 서버 제약(1~50)을 따른다. */
        const val DEFAULT_PAGE_SIZE: Int = 10
    }
}
