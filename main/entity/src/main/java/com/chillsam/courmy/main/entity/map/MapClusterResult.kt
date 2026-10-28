package com.chillsam.courmy.main.entity.map

/**
 * 뷰포트 기준 클러스터링 결과.
 *
 * [singles] 는 개별 핀으로 찍는 장소, [clusters] 는 숫자 배지로 뭉친 묶음이다.
 * 캐러셀 등 목록 UI 는 클러스터링과 무관하게 전체 장소 목록을 그대로 쓴다.
 */
data class MapClusterResult(
    val singles: List<MapPlaceVO>,
    val clusters: List<MapClusterVO>,
)
