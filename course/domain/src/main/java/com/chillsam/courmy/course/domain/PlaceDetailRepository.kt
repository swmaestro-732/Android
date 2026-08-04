package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlaceDetailVO

/**
 * 장소 **상세** 계약(`GET /service/v1/places/{placeId}`). 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 장소 **검색**은 [PlaceRepository] 가 따로 담당한다(용도가 달라 계약을 나눈다).
 *
 * [walkText] 는 장소 자체 정보가 아니라 "이 코스에서 다음 장소까지"라 호출부(코스 상세)가 넘긴다.
 */
interface PlaceDetailRepository {
    suspend fun getPlaceDetail(
        placeId: Long,
        walkText: String,
    ): PlaceDetailVO
}
