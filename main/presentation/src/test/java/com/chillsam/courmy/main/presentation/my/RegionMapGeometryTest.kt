package com.chillsam.courmy.main.presentation.my

import androidx.compose.ui.geometry.Offset
import com.chillsam.courmy.main.entity.area.AreaCoordinateVO
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaMapVO
import com.chillsam.courmy.main.entity.area.AreaPolygonVO
import com.chillsam.courmy.main.entity.area.AreaVO
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class RegionMapGeometryTest {
    private val square = listOf(Offset(0f, 0f), Offset(4f, 0f), Offset(4f, 4f), Offset(0f, 4f), Offset(0f, 0f))

    @Test
    fun `닫힌 경계 안의 점만 선택하고 경계 바깥의 점은 제외한다`() {
        assertTrue(pointInRing(Offset(2f, 2f), square))
        assertFalse(pointInRing(Offset(-1f, 2f), square))
        assertFalse(pointInRing(Offset(5f, 2f), square))
        assertFalse(pointInRing(Offset(2f, -1f), square))
        assertFalse(pointInRing(Offset(2f, 5f), square))
    }

    @Test
    fun `수평 수직 경계에 가까운 안팎의 점을 구분한다`() {
        val epsilon = 0.001f
        assertTrue(pointInRing(Offset(epsilon, 2f), square))
        assertTrue(pointInRing(Offset(4f - epsilon, 2f), square))
        assertTrue(pointInRing(Offset(2f, epsilon), square))
        assertTrue(pointInRing(Offset(2f, 4f - epsilon), square))
        assertFalse(pointInRing(Offset(-epsilon, 2f), square))
        assertFalse(pointInRing(Offset(4f + epsilon, 2f), square))
        assertFalse(pointInRing(Offset(2f, -epsilon), square))
        assertFalse(pointInRing(Offset(2f, 4f + epsilon), square))
    }

    @Test
    fun `오목한 지역의 바운딩 박스 안이라도 파인 부분은 선택되지 않는다`() {
        val concave =
            listOf(
                Offset(0f, 0f),
                Offset(4f, 0f),
                Offset(4f, 4f),
                Offset(3f, 4f),
                Offset(3f, 1f),
                Offset(1f, 1f),
                Offset(1f, 4f),
                Offset(0f, 4f),
                Offset(0f, 0f),
            )
        assertTrue(pointInRing(Offset(0.5f, 3f), concave))
        assertTrue(pointInRing(Offset(3.5f, 3f), concave))
        assertTrue(pointInRing(Offset(2f, 0.5f), concave))
        assertFalse(pointInRing(Offset(2f, 3f), concave))
    }

    @Test
    fun `링 방향과 닫힘 점 중복 여부가 선택 결과를 바꾸지 않는다`() {
        listOf(square, square.reversed(), square.dropLast(1)).forEach { ring ->
            assertTrue(pointInRing(Offset(2f, 2f), ring))
            assertFalse(pointInRing(Offset(5f, 2f), ring))
        }
    }

    @Test
    fun `먼 도서는 별도 지도로 분리하되 옹진군의 가까운 섬과 다른 시도는 본 지도에 남는다`() {
        val baekryeong = polygon(37.9, 124.6)
        val yeonpyeong = polygon(37.65, 125.7)
        val nearIsland = polygon(37.2, 126.4)
        val ulleung = polygon(37.45, 130.8)
        val dokdo = polygon(37.2, 131.8)

        assertEquals(IslandInset.BAEKRYEONG, baekryeong.islandInset("28720"))
        assertEquals(IslandInset.YEONPYEONG, yeonpyeong.islandInset("28"))
        assertNull(nearIsland.islandInset("28720"))
        assertEquals(IslandInset.ULLEUNG, ulleung.islandInset("47940"))
        assertEquals(IslandInset.DOKDO, dokdo.islandInset("47"))
        assertNull(dokdo.islandInset("11"))

        val original =
            AreaMapVO(
                AreaVO("28720", "옹진군", "인천광역시 옹진군", AreaLevel.SIGUNGU),
                listOf(baekryeong, yeonpyeong, nearIsland),
            )
        assertEquals(listOf(nearIsland), original.mainMapRegion()?.polygons)
        assertEquals(3, original.polygons.size)
    }

    @Test
    fun `배포 경계의 모든 분리된 도서는 해당 군 카드로 표시되고 본 지도와 합치면 원본이 보존된다`() {
        val regions =
            Json.parseToJsonElement(File("../data/src/main/assets/area_map.json").readText()).jsonArray.map { item ->
                val region = item.jsonObject
                val code = region.getValue("code").jsonPrimitive.content
                AreaMapVO(
                    AreaVO(code, code, code),
                    region.getValue("polygons").jsonArray.map { element ->
                        AreaPolygonVO(
                            element.jsonObject.getValue("outer").jsonArray.map { point ->
                                AreaCoordinateVO(
                                    point.jsonArray[0].jsonPrimitive.double,
                                    point.jsonArray[1].jsonPrimitive.double,
                                )
                            },
                        )
                    },
                )
            }
        val insetCounties = IslandInset.entries.map { it.county }.toSet()
        regions.forEach { region ->
            val code = region.area.code
            val main = region.mainMapRegion()?.polygons.orEmpty()
            val separate = region.polygons.filter { it.islandInset(code) != null }
            assertEquals("$code: 본 지도와 별도 지도의 원본 보존", region.polygons.toSet(), (main + separate).toSet())
            assertEquals("$code: 중복 없음", region.polygons.size, main.size + separate.size)
            if (separate.isNotEmpty()) {
                assertTrue("$code: 카드에 없는 지역이 본 지도에서 빠짐", code.length == 2 || code in insetCounties)
            }
        }
        IslandInset.entries.forEach { inset ->
            val county = regions.single { it.area.code == inset.county }
            val province = regions.single { it.area.code == inset.county.take(2) }
            assertTrue("${inset.title}: 별도 카드가 비어 있음", county.polygons.any { it.islandInset(inset.county) == inset })
            assertEquals(
                "${inset.title}: 시도에서 뺀 도서와 군 카드 개수",
                province.polygons.count { it.islandInset(province.area.code) == inset },
                county.polygons.count { it.islandInset(county.area.code) == inset },
            )
        }
        assertTrue(regions.single { it.area.code == "28720" }.mainMapRegion() != null)
        assertNull(regions.single { it.area.code == "47940" }.mainMapRegion())
    }

    private fun polygon(
        latitude: Double,
        longitude: Double,
    ) = AreaPolygonVO(
        listOf(
            AreaCoordinateVO(latitude, longitude),
            AreaCoordinateVO(latitude, longitude + 0.01),
            AreaCoordinateVO(latitude + 0.01, longitude + 0.01),
            AreaCoordinateVO(latitude + 0.01, longitude),
            AreaCoordinateVO(latitude, longitude),
        ),
    )
}
