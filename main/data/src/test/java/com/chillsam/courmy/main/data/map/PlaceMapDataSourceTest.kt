package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.PlaceMapDTO
import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope
import com.chillsam.courmy.main.data.map.dto.PlaceMapMarkerDTO
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapSortOrder
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
                    bounds = MapBounds(35.1, 129.0, 35.2, 129.1),
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
    fun `지도 10종 필터는 대문자 enum을 요청하고 소문자 응답을 공통 라벨로 표시한다`() =
        runBlocking {
            val categories =
                listOf(
                    "cafe" to "카페·디저트",
                    "restaurant" to "음식점",
                    "bar" to "술집·바",
                    "shopping" to "쇼핑·상점",
                    "culture" to "문화·전시",
                    "experience" to "체험·클래스",
                    "nature" to "자연·아웃도어",
                    "landmark" to "역사·명소",
                    "entertainment" to "여가·놀거리",
                    "wellness" to "웰니스·힐링",
                )
            assertEquals(categories.map { it.first }, MapCategory.entries.drop(1).map { it.name.lowercase() })
            (listOf(null to "") + categories).forEach { (code, label) ->
                val api =
                    FakeApi(
                        PlaceMapEnvelope(
                            code = 200,
                            data =
                                PlaceMapDTO(
                                    totalCount = 1,
                                    places =
                                        listOf(
                                            PlaceMapMarkerDTO(
                                                id = 42,
                                                category = code,
                                                latitude = 37.54,
                                                longitude = 127.05,
                                            ),
                                        ),
                                ),
                        ),
                    )
                val result =
                    PlaceMapRepositoryImpl(PlaceMapDataSource(api)).getMapPlaces(
                        swLat = 37.5,
                        swLng = 127.0,
                        neLat = 37.6,
                        neLng = 127.1,
                        category = code?.let { MapCategory.valueOf(it.uppercase()) } ?: MapCategory.ALL,
                        sortOrder = MapSortOrder.SAVED,
                        userLat = null,
                        userLng = null,
                    )
                assertEquals(code?.uppercase(), api.requestedCategory)
                assertEquals(label, result.places.single().category)
                assertEquals(1, result.totalCount)
            }
        }

    @Test
    fun `네트워크 예외를 목업 응답으로 바꾸지 않고 그대로 전파한다`() =
        runBlocking {
            val failure = IOException("네트워크 연결 실패")
            val dataSource = PlaceMapDataSource(FakeApi(failure = failure))

            val result =
                runCatching {
                    dataSource.getMapPlaces(
                        bounds = MapBounds(37.5, 127.0, 37.6, 127.1),
                        category = null,
                        sort = null,
                        userLat = null,
                        userLng = null,
                    )
                }

            assertSame(failure, result.exceptionOrNull())
        }
}
