package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.domain.search.SearchPage
import com.chillsam.courmy.main.entity.map.MapBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class MapSearchBoundsTest {
    @Test
    fun `검색 입력과 결과 route를 거쳐도 원래 지도 영역을 보존한다`() {
        val bounds = MapBounds(35.1234567, 128.9876543, 35.2345678, 129.1234567)
        val input = SearchPage.route(target = SearchPage.TARGET_PLACE, query = "카페", bounds = bounds)
        val decoded = searchBounds(input.args[SearchPage.ARG_BOUNDS])
        val result = MapSearchResultPage.route(query = "카페", bounds = decoded)

        assertEquals(bounds, decoded)
        assertEquals(bounds, searchBounds(result.args[MapSearchResultPage.ARG_BOUNDS]))
        assertEquals(bounds.toSearchArgument(), input.args[SearchPage.ARG_BOUNDS])
        assertEquals(bounds.toSearchArgument(), result.args[MapSearchResultPage.ARG_BOUNDS])
    }

    @Test
    fun `영역이 없으면 route 인자를 생략하고 좌표 범위 경계도 복원한다`() {
        assertFalse(SearchPage.route().args.containsKey(SearchPage.ARG_BOUNDS))
        assertFalse(MapSearchResultPage.route("카페").args.containsKey(MapSearchResultPage.ARG_BOUNDS))
        assertNull(searchBounds(null))
        val world = MapBounds(-90.0, -180.0, 90.0, 180.0)
        assertEquals(world, searchBounds(world.toSearchArgument()))
    }

    @Test
    fun `누락 잘못된 숫자 NaN 무한대 역전 및 범위 밖 좌표는 거부한다`() {
        val invalid =
            listOf(
                "",
                "35,129,36",
                "35,129,36,130,131",
                "35,,36,130",
                "latitude,129,36,130",
                "NaN,129,36,130",
                "35,NaN,36,130",
                "35,129,NaN,130",
                "35,129,36,NaN",
                "-Infinity,129,36,130",
                "35,129,Infinity,130",
                "35,129,36,Infinity",
                "-90.1,129,36,130",
                "35,-180.1,36,130",
                "35,129,90.1,130",
                "35,129,36,180.1",
                "36,129,35,130",
                "35,130,36,129",
                "35,129,35,130",
                "35,129,36,129",
            )
        invalid.forEach { argument -> assertNull(argument, searchBounds(argument)) }
    }
}
