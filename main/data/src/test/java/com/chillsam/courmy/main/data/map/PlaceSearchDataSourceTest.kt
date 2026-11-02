package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.MapPlaceSearchEnvelope
import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope
import com.chillsam.courmy.main.entity.map.MapBounds
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class PlaceSearchDataSourceTest {
    private val bounds = MapBounds(35.1, 129.0, 35.2, 129.1)

    private class SearchApiFixture {
        val envelope = PlaceMapEnvelope(code = 200)
        val suggestionsEnvelope = MapPlaceSearchEnvelope(code = 200)
        var requestedUrl: HttpUrl? = null
        val dataSource =
            PlaceSearchDataSource(
                Retrofit
                    .Builder()
                    .baseUrl("https://example.com/")
                    .client(
                        OkHttpClient
                            .Builder()
                            .addInterceptor { chain ->
                                requestedUrl = chain.request().url
                                Response
                                    .Builder()
                                    .request(chain.request())
                                    .protocol(Protocol.HTTP_1_1)
                                    .code(200)
                                    .message("OK")
                                    .body("{}".toResponseBody())
                                    .build()
                            }.build(),
                    ).addConverterFactory(
                        object : Converter.Factory() {
                            override fun responseBodyConverter(
                                type: Type,
                                annotations: Array<Annotation>,
                                retrofit: Retrofit,
                            ): Converter<ResponseBody, *>? =
                                if (type == PlaceMapEnvelope::class.java) {
                                    Converter<ResponseBody, PlaceMapEnvelope> { body ->
                                        body.close()
                                        envelope
                                    }
                                } else if (type == MapPlaceSearchEnvelope::class.java) {
                                    Converter<ResponseBody, MapPlaceSearchEnvelope> { body ->
                                        body.close()
                                        suggestionsEnvelope
                                    }
                                } else {
                                    null
                                }
                        },
                    ).build()
                    .create(PlaceSearchApiService::class.java),
            )
    }

    @Test
    fun `제안은 places 경로에 q와 size만 보내고 외부 검색을 호출하지 않는다`() =
        runBlocking {
            val fixture = SearchApiFixture()
            val result = fixture.dataSource.suggestions("성수 카페 & 빵")
            val url = requireNotNull(fixture.requestedUrl)

            assertEquals("/api/v1/places", url.encodedPath)
            assertEquals("성수 카페 & 빵", url.queryParameter("q"))
            assertEquals("10", url.queryParameter("size"))
            assertEquals(setOf("q", "size"), url.queryParameterNames)
            assertSame(fixture.suggestionsEnvelope, result)
        }

    @Test
    fun `지도 검색은 map 경로에 q와 뷰포트 및 위치를 전달한다`() =
        runBlocking {
            val fixture = SearchApiFixture()

            val result = fixture.dataSource.search("부산 카페 & 빵/디저트", bounds, latitude = 35.15, longitude = 129.05)
            val url = requireNotNull(fixture.requestedUrl)

            assertEquals("/api/v1/places/map", url.encodedPath)
            assertEquals("부산 카페 & 빵/디저트", url.queryParameter("q"))
            assertEquals("35.15", url.queryParameter("userLat"))
            assertEquals("129.05", url.queryParameter("userLng"))
            assertEquals(setOf("q", "swLat", "swLng", "neLat", "neLng", "userLat", "userLng"), url.queryParameterNames)
            assertEquals("35.1", url.queryParameter("swLat"))
            assertEquals("129.0", url.queryParameter("swLng"))
            assertEquals("35.2", url.queryParameter("neLat"))
            assertEquals("129.1", url.queryParameter("neLng"))
            assertNull(url.queryParameter("query"))
            assertSame(fixture.envelope, result)
        }

    @Test
    fun `위치를 모르면 검색 URL에서 두 좌표를 생략한다`() =
        runBlocking {
            val fixture = SearchApiFixture()

            fixture.dataSource.search("카페", bounds, latitude = null, longitude = null)
            val url = requireNotNull(fixture.requestedUrl)

            assertEquals("카페", url.queryParameter("q"))
            assertNull(url.queryParameter("userLat"))
            assertNull(url.queryParameter("userLng"))
            assertEquals(setOf("q", "swLat", "swLng", "neLat", "neLng"), url.queryParameterNames)
        }
}
