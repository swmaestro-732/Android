package com.chillsam.courmy.main.entity.home

/**
 * 홈 화면(FS-09) 조합 응답. `GET /service/v2/home`(BFF) 을 변환한 결과다.
 *
 * 응답에는 `profile` 도 오지만 헤더가 내 프로필 조회(`GET /service/v1/mypage`)를 이미 쓰고 있어
 * 매핑하지 않는다. 헤더를 이 응답으로 옮기면 그때 함께 싣는다.
 */
data class HomeVO(
    val recommendedCourses: List<HomeCourseVO> = emptyList(),
    /** 근처 저장 장소. 비로그인이거나 근처에 저장한 곳이 없으면 비어 있다. */
    val nearbySavedPlaces: List<HomeNearbyPlaceVO> = emptyList(),
)
