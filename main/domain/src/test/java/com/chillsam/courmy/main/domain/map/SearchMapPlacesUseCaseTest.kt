package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class SearchMapPlacesUseCaseTest {
    private class FakeRepository : MapPlaceSearchRepository {
        val page = CursorPageVO<MapPlaceVO>(nextCursor = "next+/=", hasNext = true)
        var requestedQuery: String? = null
        var requestedCursor: String? = null
        var requestedLocation: Pair<Double?, Double?>? = null

        override suspend fun search(
            query: String,
            cursor: String?,
            latitude: Double?,
            longitude: Double?,
        ): CursorPageVO<MapPlaceVO> {
            requestedQuery = query
            requestedCursor = cursor
            requestedLocation = latitude to longitude
            return page
        }
    }

    @Test
    fun `검색어 공백을 제거하고 커서와 현재 위치를 전달하며 응답을 보존한다`() =
        runTest {
            val repository = FakeRepository()

            val result = SearchMapPlacesUseCase(repository)("  카페 \n", "current+/=", 35.15, 129.05)

            assertEquals("카페", repository.requestedQuery)
            assertEquals("current+/=", repository.requestedCursor)
            assertEquals(35.15 to 129.05, repository.requestedLocation)
            assertSame(repository.page, result)
        }

    @Test
    fun `검색어 없는 목록도 저장소로 전달하고 위치 기본값은 없다`() =
        runTest {
            val repository = FakeRepository()

            SearchMapPlacesUseCase(repository)(" \t\n ", cursor = "next")

            assertEquals("", repository.requestedQuery)
            assertEquals("next", repository.requestedCursor)
            assertEquals(null to null, repository.requestedLocation)
        }

    @Test
    fun `좌표가 불완전하거나 범위 밖이면 두 좌표를 모두 생략한다`() =
        runTest {
            val repository = FakeRepository()
            val useCase = SearchMapPlacesUseCase(repository)
            val invalidLocations =
                listOf(
                    null to 127.0,
                    37.0 to null,
                    90.01 to 127.0,
                    -90.01 to 127.0,
                    37.0 to 180.01,
                    37.0 to -180.01,
                    Double.NaN to 127.0,
                    37.0 to Double.NaN,
                    Double.POSITIVE_INFINITY to 127.0,
                    37.0 to Double.NEGATIVE_INFINITY,
                )

            invalidLocations.forEach { (latitude, longitude) ->
                useCase("카페", latitude = latitude, longitude = longitude)

                assertEquals("좌표: $latitude, $longitude", null to null, repository.requestedLocation)
            }
        }

    @Test
    fun `적도와 본초 자오선 및 좌표 범위 경계는 유효하다`() =
        runTest {
            val repository = FakeRepository()
            val useCase = SearchMapPlacesUseCase(repository)

            listOf(0.0 to 0.0, -90.0 to -180.0, 90.0 to 180.0).forEach { (latitude, longitude) ->
                useCase("카페", latitude = latitude, longitude = longitude)

                assertEquals(latitude to longitude, repository.requestedLocation)
            }
        }
}
