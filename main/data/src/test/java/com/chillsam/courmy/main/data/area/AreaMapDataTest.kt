package com.chillsam.courmy.main.data.area

import com.chillsam.courmy.main.entity.area.AreaCoordinateVO
import com.chillsam.courmy.main.entity.area.AreaLevel
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AreaMapDataTest {
    @Test
    fun `배포 지도는 서버와 대응하는 시도와 시군구만 포함하며 모든 하위 지역의 부모가 있다`() {
        val regions = loadRegions()
        val provinces = regions.filter { it.area.level == AreaLevel.SIDO }.map { it.area.code }.toSet()
        val districts = regions.filter { it.area.level == AreaLevel.SIGUNGU }

        assertEquals(271, regions.size)
        assertEquals(271, regions.map { it.area.code }.distinct().size)
        assertEquals(16, provinces.size)
        assertEquals(255, districts.size)
        assertTrue(regions.none { it.area.level == AreaLevel.DONG || it.area.level == AreaLevel.UNKNOWN })
        assertTrue(districts.all { it.area.code.take(2) in provinces })
        assertTrue("36" in provinces)
        assertFalse(districts.any { it.area.code.startsWith("36") })
        assertTrue(regions.all { it.area.shortName.isNotBlank() && it.area.fullName.isNotBlank() })
    }

    @Test
    fun `배포 경계의 모든 링은 유한한 유효 좌표로 닫혀 있고 홀은 외곽과 반대 방향이다`() {
        val regions = loadRegions()
        var holeCount = 0
        regions.forEach { region ->
            assertTrue("${region.area.code}: 경계 없음", region.polygons.isNotEmpty())
            region.polygons.forEach { polygon ->
                assertRing(region.area.code, polygon.outer)
                val outerArea = signedArea(polygon.outer)
                polygon.holes.forEach { hole ->
                    holeCount++
                    assertRing(region.area.code, hole)
                    assertTrue("${region.area.code}: 홀 방향 오류", outerArea * signedArea(hole) < 0.0)
                }
            }
        }
        assertTrue("홀 검증이 실제 데이터를 검사해야 한다", holeCount > 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `DTO는 네이티브 지도에 전달할 수 없는 좌표를 거부한다`() {
        AreaPolygonDTO(
            outer =
                listOf(
                    listOf(37.0, 127.0),
                    listOf(Double.NaN, 127.1),
                    listOf(37.1, 127.1),
                    listOf(37.0, 127.0),
                ),
        ).toVO()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `DTO는 선택 깊이에 없는 동 단위 경계를 거부한다`() {
        val province = Json.decodeFromString<List<AreaMapDTO>>(asset().readText()).first()
        province.copy(code = "1111010100", level = "DONG").toVO()
    }

    private fun loadRegions() = Json.decodeFromString<List<AreaMapDTO>>(asset().readText()).map { it.toVO() }

    private fun asset() = File("src/main/assets/area_map.json")

    private fun assertRing(
        code: String,
        ring: List<AreaCoordinateVO>,
    ) {
        assertTrue("$code: 점 개수", ring.size >= 4)
        assertEquals("$code: 닫힌 링", ring.first(), ring.last())
        assertTrue("$code: 서로 다른 점", ring.distinct().size >= 3)
        assertTrue("$code: 면적 0", signedArea(ring) != 0.0)
        ring.forEach { point ->
            assertTrue("$code: 위도", point.latitude.isFinite() && point.latitude in -90.0..90.0)
            assertTrue("$code: 경도", point.longitude.isFinite() && point.longitude in -180.0..180.0)
        }
    }

    private fun signedArea(ring: List<AreaCoordinateVO>): Double {
        val origin = ring.first()
        return ring.zipWithNext().sumOf { (a, b) ->
            (a.longitude - origin.longitude) * (b.latitude - origin.latitude) -
                (b.longitude - origin.longitude) * (a.latitude - origin.latitude)
        }
    }
}
