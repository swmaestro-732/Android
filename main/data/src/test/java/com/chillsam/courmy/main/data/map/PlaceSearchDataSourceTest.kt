package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.ExternalMapPlaceSearchEnvelope
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
    private class SearchApiFixture {
        val envelope = ExternalMapPlaceSearchEnvelope(code = 200)
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
                                if (type == ExternalMapPlaceSearchEnvelope::class.java) {
                                    Converter<ResponseBody, ExternalMapPlaceSearchEnvelope> { body ->
                                        body.close()
                                        envelope
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
    fun `검색 요청은 query와 lat lng를 실제 URL로 전달한다`() =
        runBlocking {
            val fixture = SearchApiFixture()

            val result = fixture.dataSource.search("부산 카페 & 빵/디저트", latitude = 35.15, longitude = 129.05)
            val url = requireNotNull(fixture.requestedUrl)

            assertEquals("/api/v1/places/search", url.encodedPath)
            assertEquals("부산 카페 & 빵/디저트", url.queryParameter("query"))
            assertEquals("35.15", url.queryParameter("lat"))
            assertEquals("129.05", url.queryParameter("lng"))
            assertEquals(setOf("query", "lat", "lng"), url.queryParameterNames)
            assertSame(fixture.envelope, result)
        }

    @Test
    fun `위치를 모르면 검색 URL에서 두 좌표를 생략한다`() =
        runBlocking {
            val fixture = SearchApiFixture()

            fixture.dataSource.search("카페", latitude = null, longitude = null)
            val url = requireNotNull(fixture.requestedUrl)

            assertEquals("카페", url.queryParameter("query"))
            assertNull(url.queryParameter("lat"))
            assertNull(url.queryParameter("lng"))
            assertEquals(setOf("query"), url.queryParameterNames)
        }
}
