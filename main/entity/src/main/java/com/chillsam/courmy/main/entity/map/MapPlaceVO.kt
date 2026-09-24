package com.chillsam.courmy.main.entity.map

/**
 * 지도에 찍히는 장소 한 곳(FS-10).
 *
 * 좌표를 지도 SDK 타입([com.naver.maps.geometry.LatLng])이 아니라 위경도 두 값으로 들고 있는 건,
 * entity 모듈이 순수 Kotlin/JVM 을 유지해야 하기 때문이다. SDK 타입으로의 변환은 presentation 이 한다.
 * 좌표가 없는 장소(주소만 아는 곳)도 리스트에는 나와야 해서 nullable 이다.
 */
data class MapPlaceVO(
    val id: Long,
    val name: String,
    /** 대표 카테고리. 리스트 부제의 첫 항목으로 쓴다(예: "카페"). */
    val category: String,
    /** 세부 카테고리. 없을 수 있다(예: "베이커리"). */
    val subCategory: String? = null,
    val rating: Double = 0.0,
    /** 현재 위치 기준 도보 소요 시간(분). 계산할 수 없으면 null. */
    val walkingMinutes: Int? = null,
    val thumbnailUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val saved: Boolean = false,
    val openNow: Boolean = false,
    /** 영업 종료 시각 표기(예: "22:00"). 영업 정보가 없으면 null. */
    val closingTime: String? = null,
)
