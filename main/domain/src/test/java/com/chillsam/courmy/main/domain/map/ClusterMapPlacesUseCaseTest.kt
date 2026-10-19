package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClusterMapPlacesUseCaseTest {
    private val useCase = ClusterMapPlacesUseCase()

    /** 성수동 근방 1도 × 1도 뷰포트. 셀 크기는 경도 1/6 · 위도 1/10. */
    private val bounds = MapBounds(swLat = 37.0, swLng = 127.0, neLat = 38.0, neLng = 128.0)

    private fun place(
        id: Long,
        lat: Double?,
        lng: Double?,
    ) = MapPlaceVO(id = id, name = "p$id", category = "CAFE", latitude = lat, longitude = lng)

    @Test
    fun `같은 칸에 3개 이상 몰리면 개수와 중심 좌표로 묶는다`() {
        val packed =
            listOf(
                place(1, 37.01, 127.01),
                place(2, 37.02, 127.02),
                place(3, 37.03, 127.03),
            )
        val result = useCase(packed, bounds)

        assertTrue(result.singles.isEmpty())
        assertEquals(1, result.clusters.size)
        assertEquals(3, result.clusters.single().count)
        assertEquals(37.02, result.clusters.single().latitude, 1e-9)
        assertEquals(127.02, result.clusters.single().longitude, 1e-9)
    }

    @Test
    fun `두 개까지는 묶지 않는다`() {
        val pair = listOf(place(1, 37.01, 127.01), place(2, 37.02, 127.02))
        val result = useCase(pair, bounds)

        assertEquals(pair, result.singles)
        assertTrue(result.clusters.isEmpty())
    }

    @Test
    fun `선택된 장소가 든 칸은 묶지 않는다 - 말풍선이 가려지면 안 된다`() {
        val packed =
            listOf(
                place(1, 37.01, 127.01),
                place(2, 37.02, 127.02),
                place(3, 37.03, 127.03),
            )
        val result = useCase(packed, bounds, selectedPlaceId = 2)

        assertEquals(packed, result.singles)
        assertTrue(result.clusters.isEmpty())
    }

    @Test
    fun `칸이 다르면 각자 단독 핀으로 남는다`() {
        val spread =
            listOf(
                place(1, 37.05, 127.05),
                place(2, 37.55, 127.55),
                place(3, 37.95, 127.95),
            )
        val result = useCase(spread, bounds)

        assertEquals(3, result.singles.size)
        assertTrue(result.clusters.isEmpty())
    }

    @Test
    fun `뷰포트가 없거나 좌표가 없는 장소는 묶지 않고 그대로 돌려준다`() {
        val noCoord = listOf(place(1, null, null), place(2, 37.01, 127.01), place(3, 37.02, null))

        val noBounds = useCase(noCoord, bounds = null)
        assertEquals(noCoord, noBounds.singles)
        assertTrue(noBounds.clusters.isEmpty())

        val withBounds = useCase(noCoord, bounds)
        assertEquals(3, withBounds.singles.size)
        assertTrue(withBounds.clusters.isEmpty())
    }
}
