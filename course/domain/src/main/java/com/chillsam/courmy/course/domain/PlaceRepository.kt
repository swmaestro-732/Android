package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CoursePlaceVO

/** 장소 검색 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다. */
interface PlaceRepository {
    /**
     * 이름으로 장소를 검색한다. 결과의 id 는 서버 place id 라 코스 생성 요청에 그대로 쓸 수 있다.
     * 검색어가 비면 호출하지 않는 것을 전제로 한다(화면에서 거른다).
     */
    suspend fun searchPlaces(query: String): List<CoursePlaceVO>

    /**
     * 외부 지도(카카오)에서 장소를 찾는다. 등록되지 않은 장소도 나오며,
     * 서버가 검색 시점에 내부 저장까지 마쳐 결과 id 를 코스 생성에 그대로 쓸 수 있다.
     */
    suspend fun searchExternalPlaces(query: String): List<CoursePlaceVO>
}
