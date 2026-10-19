package com.chillsam.courmy.main.domain.search

import org.junit.Assert.assertEquals
import org.junit.Test

class SearchRouteTest {
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
