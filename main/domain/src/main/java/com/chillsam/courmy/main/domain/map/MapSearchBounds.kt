package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.entity.map.MapBounds

/** 입력 화면을 거쳐도 검색을 시작한 실제 지도 영역을 보존한다. */
fun MapBounds.toSearchArgument(): String = listOf(swLat, swLng, neLat, neLng).joinToString(",")

fun searchBounds(argument: String?): MapBounds? {
    val values = argument?.split(",")?.map { it.toDoubleOrNull() } ?: return null
    if (values.size != 4 || values.any { it == null }) return null
    val coordinates = values.filterNotNull()
    val swLat = coordinates[0]
    val swLng = coordinates[1]
    val neLat = coordinates[2]
    val neLng = coordinates[3]
    val validLatitudes = swLat in -90.0..90.0 && neLat in -90.0..90.0 && swLat < neLat
    val validLongitudes = swLng in -180.0..180.0 && neLng in -180.0..180.0 && swLng < neLng
    return if (validLatitudes && validLongitudes) MapBounds(swLat, swLng, neLat, neLng) else null
}
