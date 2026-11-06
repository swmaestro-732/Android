package com.chillsam.courmy.main.entity.map

/** 같은 목적지라도 새 검색은 별개의 카메라 이동으로 처리한다. */
data class MapCameraMove(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
)
