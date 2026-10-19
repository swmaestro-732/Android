package com.chillsam.courmy.main.entity.map

/**
 * 지도에서 여러 장소가 한 점으로 뭉친 묶음(FS-10 의 `+9` 배지).
 *
 * 화면 밖이거나 서로 너무 가까워 개별 핀으로 찍으면 겹치는 장소들을 대표한다.
 * 어떤 장소가 묶였는지는 탭했을 때 확대해서 풀면 되므로 개수만 들고 있는다.
 */
data class MapClusterVO(
    val latitude: Double,
    val longitude: Double,
    val count: Int,
)
