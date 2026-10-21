package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapClusterResult
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import javax.inject.Inject
import kotlin.math.abs

/**
 * 화면에서 서로 겹치는 핀을 전부 하나의 숫자 묶음으로 합친다(SCRUM-633).
 *
 * "겹침"은 핀 중심 간 거리가 핀 지름(뷰포트 폭 비례) 이내인 것이고, A-B 가 겹치고 B-C 가 겹치면
 * A·B·C 를 한 묶음으로 본다(연결 요소). 기준 핀 반경만 보는 방식은 묶음 바로 옆에 겹친 쌍이
 * 남는 비일관이 있어 이렇게 계산한다. 줌을 당기면 반경이 지리적으로 좁아져 자연히 풀린다.
 *
 * - 좌표 없는 장소는 핀을 못 찍으므로 그대로 단독 목록에 남긴다(지도 쪽에서 걸러진다).
 * - 선택된 장소가 든 묶음은 만들지 않는다 — 말풍선이 숫자 배지 뒤로 사라지면 안 된다.
 */
class ClusterMapPlacesUseCase
    @Inject
    constructor() {
        operator fun invoke(
            places: List<MapPlaceVO>,
            bounds: MapBounds?,
            selectedPlaceId: Long? = null,
        ): MapClusterResult {
            val radius = bounds?.overlapRadius() ?: return MapClusterResult(places, emptyList())
            val (pinned, unpinned) = places.partition { it.latitude != null && it.longitude != null }

            val singles = unpinned.toMutableList()
            val clusters = mutableListOf<MapClusterVO>()
            val visited = HashSet<Long>()
            pinned.forEach { seed ->
                if (seed.id in visited) return@forEach
                val component = collectOverlapping(seed, pinned, visited, radius)
                if (component.size < MIN_CLUSTER_SIZE || component.any { it.id == selectedPlaceId }) {
                    singles += component
                } else {
                    clusters += component.toCluster()
                }
            }
            return MapClusterResult(singles = singles, clusters = clusters)
        }

        /** [seed] 에서 시작해 겹침이 이어지는 핀을 BFS 로 전부 모은다. 모은 핀은 [visited] 에 기록된다. */
        private fun collectOverlapping(
            seed: MapPlaceVO,
            pinned: List<MapPlaceVO>,
            visited: MutableSet<Long>,
            radius: OverlapRadius,
        ): List<MapPlaceVO> {
            val component = mutableListOf(seed)
            visited += seed.id
            var index = 0
            while (index < component.size) {
                val current = component[index++]
                pinned.forEach { other ->
                    if (other.id !in visited && radius.overlaps(current, other)) {
                        visited += other.id
                        component += other
                    }
                }
            }
            return component
        }

        private fun List<MapPlaceVO>.toCluster(): MapClusterVO =
            MapClusterVO(
                latitude = mapNotNull { it.latitude }.average(),
                longitude = mapNotNull { it.longitude }.average(),
                count = size,
            )

        /** 겹침 판정 반경. 위경도 단위라 뷰포트가 바뀔 때마다 새로 만든다. */
        private data class OverlapRadius(
            val lat: Double,
            val lng: Double,
        ) {
            fun overlaps(
                a: MapPlaceVO,
                b: MapPlaceVO,
            ): Boolean =
                abs(requireNotNull(a.latitude) - requireNotNull(b.latitude)) <= lat &&
                    abs(requireNotNull(a.longitude) - requireNotNull(b.longitude)) <= lng
        }

        private fun MapBounds.overlapRadius(): OverlapRadius? {
            val lng = (neLng - swLng) / LNG_DIVISOR
            val lat = (neLat - swLat) / LAT_DIVISOR
            return if (lat > 0.0 && lng > 0.0) OverlapRadius(lat = lat, lng = lng) else null
        }

        private companion object {
            /** 겹침 반경 = 뷰포트 가로폭 / 10. 핀 지름(~40dp)이 화면 폭의 1/10 수준이다. */
            const val LNG_DIVISOR = 10

            /** 세로 반경 비율. 화면 비율(약 1:2)만큼 더 촘촘하다. */
            const val LAT_DIVISOR = 18

            /** 겹치면 둘부터 묶는다 — 겹친 심볼이 따로 남아 있으면 안 된다(사용자 결정). */
            const val MIN_CLUSTER_SIZE = 2
        }
    }
