package com.chillsam.courmy.main.entity.home

/**
 * 홈(FS-09) "근처 저장 장소" 카드 1건. `GET /service/v2/home` 의 `nearbySavedPlaces` 를 변환한 결과다.
 *
 * 지도 탭의 [com.chillsam.courmy.main.entity.map.MapPlaceVO] 와 비슷하지만,
 * 홈 카드에만 있는 방문 여부([visited])가 있고 지도에만 필요한 좌표·영업 정보가 없어 따로 둔다.
 *
 * - [ratingText]  평점 표시 텍스트(예: "4.8"). 평가가 아직 없으면 빈 문자열이며 별 아이콘째 숨긴다.
 */
data class HomeNearbyPlaceVO(
    val id: Long,
    val name: String,
    val thumbnailUrl: String = "",
    /** 카테고리 한글 라벨(예: "카페·디저트"). 서버 코드(`cafe` …)를 변환한 값. */
    val categoryLabel: String = "",
    val ratingText: String = "",
    /** 현재 위치 기준 도보 소요 시간(분). 계산할 수 없으면 null. */
    val walkingMinutes: Int? = null,
    val visited: Boolean = false,
)
