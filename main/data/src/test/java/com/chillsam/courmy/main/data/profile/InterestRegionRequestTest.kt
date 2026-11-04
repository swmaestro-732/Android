package com.chillsam.courmy.main.data.profile

import android.content.Context
import android.content.res.AssetManager
import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.data.area.LegalAreaCodeMapper
import com.chillsam.courmy.main.data.auth.AuthDataSource
import com.chillsam.courmy.main.data.auth.AuthRepositoryImpl
import com.chillsam.courmy.main.data.auth.dto.SignupDataDTO
import com.chillsam.courmy.main.data.auth.dto.SignupEnvelope
import com.chillsam.courmy.main.data.auth.dto.SignupRequest
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileEnvelope
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileRequest
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.entity.auth.SignupProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.IOException

class InterestRegionRequestTest {
    private val tokenStore = mockk<TokenStore>(relaxed = true)
    private val interestStore = mockk<InterestPreferencesDataStore>(relaxed = true)
    private val profileSource = mockk<ProfileDataSource>()
    private val seoul = AreaVO("11", "서울특별시", "서울특별시", AreaLevel.SIDO)

    @Test
    fun `가입 요청에는 화면 prefix 대신 법정동 문자열 배열이 실린다`() =
        runBlocking {
            val source = mockk<AuthDataSource>()
            val request = slot<SignupRequest>()
            every { tokenStore.registrationToken } returns "registration"
            coEvery { source.signup(capture(request)) } returns
                SignupEnvelope(data = SignupDataDTO(accessToken = "access"))
            AuthRepositoryImpl(
                source,
                tokenStore,
                mockk(relaxed = true),
                mapper(),
            ).signup(SignupProfile("닉네임", "handle", areaCodes = listOf("11")))

            val serialized = Json.parseToJsonElement(Json.encodeToString(request.captured)).jsonObject
            assertEquals(seoulCodes(), serialized.getValue("areaCodes").jsonArray.map { it.jsonPrimitive.content })
            assertTrue(requireNotNull(request.captured.areaCodes).all { it.length == 10 })
        }

    @Test
    fun `프로필 저장은 법정동 배열 전송 후 화면의 시도 선택 하나를 캐시한다`() =
        runBlocking {
            val request = slot<UpdateProfileRequest>()
            coEvery { profileSource.updateProfile(capture(request)) } returns UpdateProfileEnvelope(code = 200)
            repository().saveInterestRegions(listOf(seoul))

            assertEquals(seoulCodes(), request.captured.areaCodes)
            coVerify(exactly = 1) { interestStore.setRegions(listOf(seoul)) }
        }

    @Test
    fun `프로필 저장 실패는 기존 캐시를 덮어쓰지 않는다`() =
        runBlocking {
            coEvery { profileSource.updateProfile(any()) } throws IOException("offline")
            val result = runCatching { repository().saveInterestRegions(listOf(seoul)) }
            assertTrue(result.exceptionOrNull() is IOException)
            coVerify(exactly = 0) { interestStore.setRegions(any()) }
        }

    @Test
    fun `변환 불가능한 선택은 프로필 API와 캐시 모두 변경하지 않는다`() =
        runBlocking {
            val result = runCatching { repository().saveInterestRegions(listOf(seoul, seoul.copy(code = "99"))) }
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            coVerify(exactly = 0) { profileSource.updateProfile(any()) }
            coVerify(exactly = 0) { interestStore.setRegions(any()) }
        }

    private fun repository() = ProfileRepositoryImpl(profileSource, tokenStore, mapper(), interestStore)

    private fun mapper(): LegalAreaCodeMapper {
        val context = mockk<Context>()
        val assets = mockk<AssetManager>()
        every { context.assets } returns assets
        every { assets.open("legal_areas.json") } answers { File("src/main/assets/legal_areas.json").inputStream() }
        return LegalAreaCodeMapper(context)
    }

    private fun seoulCodes(): List<String> =
        Json
            .parseToJsonElement(File("src/main/assets/legal_areas.json").readText())
            .jsonArray
            .map {
                it.jsonObject
                    .getValue("code")
                    .jsonPrimitive.content
            }.filter { it.startsWith("11") }
            .sorted()
}
