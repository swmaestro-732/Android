package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.main.domain.map.MapSearchResultPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SearchRouteTest {
    @Test
    fun `지도 검색 입력과 결과 경로는 현재 위치와 목록 모드를 보존한다`() {
        val input = SearchPage.route(SearchPage.TARGET_PLACE, "부산 카페", latitude = 35.15, longitude = 129.05)
        val result = MapSearchResultPage.route("부산 카페", listMode = true, latitude = 35.15, longitude = 129.05)

        assertEquals("35.15", input.args[SearchPage.ARG_LATITUDE])
        assertEquals("129.05", input.args[SearchPage.ARG_LONGITUDE])
        assertEquals(MapSearchResultPage.PATH, result.path)
        assertEquals("부산 카페", result.args[MapSearchResultPage.ARG_QUERY])
        assertEquals("true", result.args[MapSearchResultPage.ARG_LIST_MODE])
        assertEquals("35.15", result.args[MapSearchResultPage.ARG_LATITUDE])
        assertEquals("129.05", result.args[MapSearchResultPage.ARG_LONGITUDE])
    }

    @Test
    fun `위치 없는 검색 경로는 좌표 인자를 생략한다`() {
        val input = SearchPage.route()
        val result = MapSearchResultPage.route("카페")

        assertNull(input.args[SearchPage.ARG_LATITUDE])
        assertNull(input.args[SearchPage.ARG_LONGITUDE])
        assertNull(result.args[MapSearchResultPage.ARG_LATITUDE])
        assertNull(result.args[MapSearchResultPage.ARG_LONGITUDE])
        assertEquals("false", result.args[MapSearchResultPage.ARG_LIST_MODE])
    }

    @Test
    fun `기본 검색 진입은 빈 검색어의 코스 검색이다`() {
        val route = SearchPage.toRoute()

        assertEquals(SearchPage.PATH, route.path)
        assertEquals("course", route.args[SearchPage.ARG_TARGET])
        assertEquals("", route.args[SearchPage.ARG_QUERY])
        assertEquals(route, SearchPage.route())
    }

    @Test
    fun `지도 검색 진입은 장소 대상과 기존 검색어를 유지한다`() {
        val query = "성수 카페 & 빵/디저트"
        val route = SearchPage.route(target = SearchPage.TARGET_PLACE, query = query)

        assertEquals(SearchPage.PATH, route.path)
        assertEquals("place", route.args[SearchPage.ARG_TARGET])
        assertEquals(query, route.args[SearchPage.ARG_QUERY])
    }

    @Test
    fun `코스 검색 진입과 결과는 검색어를 args로 전달한다`() {
        val query = "성수 산책 & 맛집/카페"
        val input = SearchPage.route(target = SearchPage.TARGET_COURSE, query = query)
        val result = SearchResultPage.route(query)

        assertEquals("course", input.args[SearchPage.ARG_TARGET])
        assertEquals(query, input.args[SearchPage.ARG_QUERY])
        assertEquals(SearchResultPage.PATH, result.path)
        assertEquals(mapOf(SearchResultPage.ARG_QUERY to query), result.args)
    }
}
