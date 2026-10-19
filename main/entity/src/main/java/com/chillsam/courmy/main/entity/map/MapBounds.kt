package com.chillsam.courmy.main.entity.map

/** 화면에 표시된 지도 영역의 남서·북동 좌표. */
data class MapBounds(
    val swLat: Double,
    val swLng: Double,
    val neLat: Double,
    val neLng: Double,
)
