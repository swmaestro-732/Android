package com.chillsam.courmy.main.presentation.aichat

import android.location.Address
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import kotlin.math.cos

class AiChatRegionTest {
    @Test
    fun `동 대신 구를 우선하고 앞뒤 공백을 제거한다`() {
        val address = address(thoroughfare = "왕십리로", subLocality = "성수동", subAdminArea = " 성동구 ", locality = "서울특별시")
        assertEquals("성동구", address.toAiChatRegionName())
    }

    @Test
    fun `구 군이 없으면 시를 사용한다`() {
        assertEquals("수원시", address(subLocality = "매산동", locality = "수원시").toAiChatRegionName())
        assertEquals("제주시", address(subAdminArea = "조천읍", locality = "제주시").toAiChatRegionName())
        assertEquals("서울특별시", address(locality = "서울특별시").toAiChatRegionName())
    }

    @Test
    fun `구 군이 있으면 시보다 우선한다`() {
        assertEquals("영통구", address(subLocality = "영통구", locality = "수원시").toAiChatRegionName())
        assertEquals("양평군", address(subLocality = "양평읍", subAdminArea = "양평군").toAiChatRegionName())
    }

    @Test
    fun `도로명 동 읍 면 도 빈 주소는 지역 칩 이름으로 쓰지 않는다`() {
        assertNull(address(thoroughfare = "왕십리로", subLocality = "성수동", subAdminArea = "경기도").toAiChatRegionName())
        assertNull(address(subLocality = "조천읍", locality = "구좌면").toAiChatRegionName())
        assertNull(address().toAiChatRegionName())
    }

    @Test
    fun `현재 위치부터 약 1점5km 북 동 남 서 순서로 주변을 조회한다`() =
        runTest {
            val latitude = 37.55
            val longitude = 127.04
            val visited = mutableListOf<Pair<Double, Double>>()
            val result =
                nearbyAiChatRegionNames(latitude, longitude) { lat, lng ->
                    visited += lat to lng
                    null
                }

            assertTrue(result.isEmpty())
            assertEquals(5, visited.size)
            assertEquals(latitude to longitude, visited[0])
            val north = visited[1]
            val east = visited[2]
            val south = visited[3]
            val west = visited[4]
            assertEquals(longitude, north.second, 0.000001)
            assertEquals(latitude, east.first, 0.000001)
            assertEquals(longitude, south.second, 0.000001)
            assertEquals(latitude, west.first, 0.000001)
            assertEquals(1.5, (north.first - latitude) * 111.0, 0.1)
            assertEquals(1.5, (east.second - longitude) * 111.0 * cos(Math.toRadians(latitude)), 0.1)
            assertEquals(1.5, (latitude - south.first) * 111.0, 0.1)
            assertEquals(1.5, (longitude - west.second) * 111.0 * cos(Math.toRadians(latitude)), 0.1)
        }

    @Test
    fun `주변 지역 이름은 중복과 빈값을 제외하고 조회 순서를 유지한다`() =
        runTest {
            val names = listOf("성수동", "성수동", "  ", null, "송정동").iterator()
            val result = nearbyAiChatRegionNames(37.55, 127.04) { _, _ -> names.next() }
            assertEquals(listOf("성수동", "송정동"), result)
        }

    @Test
    fun `서로 다른 지역이 많아도 최대 세 개만 표시한다`() =
        runTest {
            val names = listOf("성수동", "송정동", "화양동", "자양동", "왕십리동").iterator()
            val result = nearbyAiChatRegionNames(37.55, 127.04) { _, _ -> names.next() }
            assertEquals(listOf("성수동", "송정동", "화양동"), result)
        }

    @Test
    fun `한 지점 조회가 실패해도 이후 지점에서 지역을 찾는다`() =
        runTest {
            var calls = 0
            val result =
                nearbyAiChatRegionNames(37.55, 127.04) { _, _ ->
                    when (calls++) {
                        0 -> throw IOException("geocoder unavailable")
                        1 -> "송정동"
                        else -> null
                    }
                }
            assertEquals(listOf("송정동"), result)
            assertEquals(5, calls)
        }

    @Test
    fun `조회 취소는 빈 결과로 숨기지 않고 호출자에게 전파한다`() =
        runTest {
            val cancellation = CancellationException("screen closed")
            var calls = 0
            var caught: CancellationException? = null
            try {
                nearbyAiChatRegionNames(37.55, 127.04) { _, _ ->
                    calls++
                    throw cancellation
                }
            } catch (e: CancellationException) {
                caught = e
            }
            assertSame(cancellation, caught)
            assertEquals(1, calls)
        }

    private fun address(
        thoroughfare: String? = null,
        subLocality: String? = null,
        subAdminArea: String? = null,
        locality: String? = null,
    ): Address =
        mockk {
            every { getThoroughfare() } returns thoroughfare
            every { getSubLocality() } returns subLocality
            every { getSubAdminArea() } returns subAdminArea
            every { getLocality() } returns locality
        }
}
