package com.chillsam.courmy.course.domain

/**
 * 장소 저장 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 *
 * 코스 저장([CourseSaveRepository])과 같은 구조다 — 장소 상세 시트(course)와 저장함·지도(main)가
 * 함께 쓰는 관심사라 [PlaceDetailRepository] 에 얹지 않고 나눠 둔다.
 * 사용자는 서버가 JWT 로 식별하므로 인자에 id 를 싣지 않는다(로그인 필수).
 */
interface PlaceSaveRepository {
    /** 저장([saved]=true, `POST /api/v1/saved-places`) 또는 취소(false, `DELETE /api/v1/saved-places/{placeId}`). */
    suspend fun setSaved(
        placeId: Long,
        saved: Boolean,
    )

    /**
     * 저장한 장소를 "방문함"으로 표시한다(`PATCH /api/v1/saved-places/{placeId}`).
     * 저장하지 않은 장소면 서버가 404 로 거절한다.
     */
    suspend fun markVisited(placeId: Long)
}
