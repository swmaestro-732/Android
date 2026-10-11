package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.PlaceMapDTO
import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope
import com.chillsam.courmy.main.data.map.dto.PlaceMapMarkerDTO
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class PlaceMapDataSourceTest {
    private class FakeApi(
        private val response: PlaceMapEnvelope = PlaceMapEnvelope(),
        private val failure: IOException? = null,
    ) : PlaceMapApiService {
        var requestedBounds: List<Double>? = null
        var requestedCategory: String? = null
        var requestedSort: String? = null
        var requestedLocation: List<Double?>? = null

        override suspend fun getMapPlaces(
            swLat: Double,
            swLng: Double,
            neLat: Double,
            neLng: Double,
            category: String?,
            sort: String?,
            userLat: Double?,
            userLng: Double?,
        ): Response<PlaceMapEnvelope> {
            requestedBounds = listOf(swLat, swLng, neLat, neLng)
            requestedCategory = category
            requestedSort = sort
            requestedLocation = listOf(userLat, userLng)
            failure?.let { throw it }
            return Response.success(response)
        }
    }

    @Test
    fun `실제 요청 영역과 필터를 전달하고 서버 성공 응답을 그대로 반환한다`() =
        runBlocking {
            val response =
                PlaceMapEnvelope(
                    code = 200,
                    data =
                        PlaceMapDTO(
                            totalCount = 1,
                            places = listOf(PlaceMapMarkerDTO(id = 42, name = "서버 장소")),
                        ),
                )
            val api = FakeApi(response)

            val result =
                PlaceMapDataSource(api).getMapPlaces(
                    swLat = 35.1,
                    swLng = 129.0,
                    neLat = 35.2,
                    neLng = 129.1,
                    category = "CAFE",
                    sort = "DISTANCE",
                    userLat = 35.15,
                    userLng = 129.05,
                )

            assertSame(response, result)
            assertEquals(listOf(35.1, 129.0, 35.2, 129.1), api.requestedBounds)
            assertEquals("CAFE", api.requestedCategory)
            assertEquals("DISTANCE", api.requestedSort)
            assertEquals(listOf(35.15, 129.05), api.requestedLocation)
        }

    @Test
    fun `네트워크 예외를 목업 응답으로 바꾸지 않고 그대로 전파한다`() =
        runBlocking {
            val failure = IOException("네트워크 연결 실패")
            val dataSource = PlaceMapDataSource(FakeApi(failure = failure))

            val result =
                runCatching {
                    dataSource.getMapPlaces(
                        swLat = 37.5,
                        swLng = 127.0,
                        neLat = 37.6,
                        neLng = 127.1,
                        category = null,
                        sort = null,
                        userLat = null,
                        userLng = null,
                    )
                }

            assertSame(failure, result.exceptionOrNull())
        }
}
