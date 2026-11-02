package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapPlacesVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SuggestMapPlacesUseCaseTest {
    @Test
    fun `유효한 장소만 서버 관련도 순서대로 유지한다`() =
        runTest {
            val first = place(30, 35.15, 129.05)
            val second = place(10, 0.0, 0.0)
            val third = place(20, -90.0, -180.0)
            val repository =
                FakeRepository(
                    listOf(
                        place(0, 37.5, 127.0),
                        first,
                        place(2, null, 127.0),
                        place(3, 37.5, null),
                        place(4, Double.NaN, 127.0),
                        second,
                        place(5, 90.01, 127.0),
                        place(6, 37.5, 180.01),
                        place(7, Double.NEGATIVE_INFINITY, 127.0),
                        third,
                    ),
                )

            val result = SuggestMapPlacesUseCase(repository)("  성수 카페 \n")

            assertEquals(listOf("성수 카페"), repository.queries)
            assertEquals(listOf(first, second, third), result)
        }

    @Test
    fun `빈 키워드는 서버를 호출하지 않는다`() =
        runTest {
            val repository = FakeRepository(emptyList())
            assertTrue(SuggestMapPlacesUseCase(repository)(" \t\n ").isEmpty())
            assertTrue(repository.queries.isEmpty())
        }

    private fun place(
        id: Long,
        latitude: Double?,
        longitude: Double?,
    ) = MapPlaceVO(id, "장소 $id", "카페", latitude = latitude, longitude = longitude)

    private class FakeRepository(
        private val places: List<MapPlaceVO>,
    ) : MapPlaceSearchRepository {
        val queries = mutableListOf<String>()

        override suspend fun suggestions(query: String): List<MapPlaceVO> {
            queries += query
            return places
        }

        override suspend fun search(
            query: String,
            bounds: MapBounds,
            category: MapCategory,
            latitude: Double?,
            longitude: Double?,
        ): MapPlacesVO = error("제안 조회에서 지도 영역 API를 호출하면 안 됩니다.")
    }
}
