package com.chillsam.courmy.main.entity.map

/**
 * 지도 영역(bounds) 탐색 결과 한 묶음(FS-10).
 *
 * [totalCount] 는 조건에 맞는 전체 개수라 [places] 크기와 다를 수 있다 — 서버가 지도에 찍을
 * 개별 핀과 뭉친 묶음([clusters])으로 나눠 주기 때문이다.
 */
data class MapPlacesVO(
    val totalCount: Int = 0,
    val places: List<MapPlaceVO> = emptyList(),
    val clusters: List<MapClusterVO> = emptyList(),
)
