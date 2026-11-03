package com.chillsam.courmy.main.data.area

import android.content.Context
import android.content.res.AssetManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LegalAreaCodeMapperTest {
    @Test
    fun `시도와 시군구는 활성 법정동으로 펼치고 패딩과 중복을 제거한다`() =
        runBlocking {
            val codes = listOf("1120010100", "1120010200", "1144010100", "2811010100")
            assertEquals(codes.take(3), expandLegalAreaCodes(listOf("11", "11200", "1100000000", "1120010100"), codes))
            assertEquals(codes.take(2), expandLegalAreaCodes(listOf("1120000000"), codes))
            assertEquals(listOf("2811010100"), expandLegalAreaCodes(listOf("2811010100"), codes))
            assertEquals(emptyList<String>(), expandLegalAreaCodes(emptyList(), codes))
        }

    @Test
    fun `알 수 없는 코드가 섞여 있으면 일부 저장 대신 실패한다`() {
        val codes = listOf("1120010100")
        listOf("", " 11", "1120a", "123456", "99", "9999900000", "1120010199").forEach { invalid ->
            val result = runCatching { expandLegalAreaCodes(listOf("11", invalid), codes) }
            assertTrue("입력 $invalid 거부", result.exceptionOrNull() is IllegalArgumentException)
        }
    }

    @Test
    fun `다운로드 기반 asset은 읍면동 5067개이며 모든 지도 선택을 빠짐없이 변환한다`() =
        runBlocking {
            val legalCodes = assetCodes()
            val mapRegions = Json.decodeFromString<List<AreaMapDTO>>(File("src/main/assets/area_map.json").readText())
            assertEquals(5067, legalCodes.size)
            assertEquals(legalCodes.size, legalCodes.distinct().size)
            assertTrue(legalCodes.all { it.length == 10 && it.endsWith("00") && !it.endsWith("00000") })
            mapRegions.forEach { region ->
                val result = expandLegalAreaCodes(listOf(region.code), legalCodes)
                assertTrue("${region.code}: 빈 변환", result.isNotEmpty())
                assertEquals(legalCodes.filter { it.startsWith(region.code) }.sorted(), result)
            }
            val nation = mapRegions.filter { it.level == "SIDO" }.map { it.code }
            assertEquals(legalCodes.sorted(), expandLegalAreaCodes(nation, legalCodes))
            assertTrue(expandLegalAreaCodes(listOf("36"), legalCodes).all { it.startsWith("36110") })
        }

    @Test
    fun `전체에서 옹진군을 제외한 요청에는 다른 인천 법정동만 들어간다`() {
        val codes = assetCodes()
        val districts = codes.filter { it.startsWith("28") }.map { it.take(5) }.distinct()
        val selected = districts.filterNot { it == "28720" }
        assertEquals(
            codes
                .filter {
                    it.startsWith("28") && !it.startsWith("28720")
                }.sorted(),
            expandLegalAreaCodes(selected, codes),
        )
    }

    @Test
    fun `asset은 첫 요청에만 읽고 빈 선택에는 읽지 않는다`() =
        runBlocking {
            val context = mockk<Context>()
            val assets = mockk<AssetManager>()
            every { context.assets } returns assets
            every { assets.open("legal_areas.json") } answers { File("src/main/assets/legal_areas.json").inputStream() }
            val mapper = LegalAreaCodeMapper(context)
            assertEquals(emptyList<String>(), mapper.expand(emptyList()))
            verify(exactly = 0) { assets.open(any()) }
            assertEquals(assetCodes().filter { it.startsWith("11") }.sorted(), mapper.expand(listOf("11")))
            mapper.expand(listOf("28"))
            verify(exactly = 1) { assets.open("legal_areas.json") }
        }

    private fun assetCodes(): List<String> =
        Json.parseToJsonElement(File("src/main/assets/legal_areas.json").readText()).jsonArray.map {
            it.jsonObject
                .getValue("code")
                .jsonPrimitive.content
        }
}
