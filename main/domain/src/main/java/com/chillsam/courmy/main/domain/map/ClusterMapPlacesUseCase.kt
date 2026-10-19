package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapClusterResult
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import javax.inject.Inject
import kotlin.math.floor

/**
 * 뷰포트를 격자로 나눠 같은 칸에 몰린 핀을 숫자 묶음으로 바꾼다(SCRUM-633).
 *
 * 격자 크기가 화면 폭 기준이라 줌을 당기면 칸이 좁아져 자연히 풀린다. 서버 클러스터와 별개로
 * 화면에 실제로 겹치는 핀만 정리하는 표시용 규칙이므로 도메인 데이터는 바꾸지 않는다.
 *
 * - 좌표 없는 장소는 핀을 못 찍으므로 그대로 단독 목록에 남긴다(지도 쪽에서 걸러진다).
 * - 선택된 장소가 든 칸은 묶지 않는다 — 말풍선이 숫자 배지 뒤로 사라지면 안 된다.
 */
class ClusterMapPlacesUseCase
    @Inject
    constructor() {
        operator fun invoke(
            places: List<MapPlaceVO>,
            bounds: MapBounds?,
            selectedPlaceId: Long? = null,
        ): MapClusterResult {
            val cellWidth = bounds?.let { (it.neLng - it.swLng) / LNG_CELLS } ?: 0.0
            val cellHeight = bounds?.let { (it.neLat - it.swLat) / LAT_CELLS } ?: 0.0
            if (bounds == null || cellWidth <= 0.0 || cellHeight <= 0.0) {
                return MapClusterResult(singles = places, clusters = emptyList())
            }

            val singles = mutableListOf<MapPlaceVO>()
            val clusters = mutableListOf<MapClusterVO>()
            places
                .groupBy { place ->
                    val lat = place.latitude
                    val lng = place.longitude
                    if (lat == null || lng == null) {
                        null
                    } else {
                        floor((lat - bounds.swLat) / cellHeight).toInt() to
                            floor((lng - bounds.swLng) / cellWidth).toInt()
                    }
                }.forEach { (cell, group) ->
                    val keepApart =
                        cell == null ||
                            group.size < MIN_CLUSTER_SIZE ||
                            group.any { it.id == selectedPlaceId }
                    if (keepApart) {
                        singles += group
                    } else {
                        clusters +=
                            MapClusterVO(
                                latitude = group.mapNotNull { it.latitude }.average(),
                                longitude = group.mapNotNull { it.longitude }.average(),
                                count = group.size,
                            )
                    }
                }
            return MapClusterResult(singles = singles, clusters = clusters)
        }

        private companion object {
            /** 뷰포트 가로를 나누는 칸 수. 핀 지름(~40dp)이 한 칸에 들어오는 정도. */
            const val LNG_CELLS = 6

            /** 세로 칸 수. 화면 비율(약 1:2)에 맞춰 가로보다 촘촘히 나눈다. */
            const val LAT_CELLS = 10

            /** 이 수 이상 몰렸을 때만 묶는다. 둘 정도는 겹쳐도 각자 탭할 수 있다. */
            const val MIN_CLUSTER_SIZE = 3
        }
    }
