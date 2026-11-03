package com.chillsam.courmy.main.data.profile

import android.content.Context
import android.content.res.AssetManager
import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.data.area.LegalAreaCodeMapper
import com.chillsam.courmy.main.data.profile.dto.AccountProfileEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope
import com.chillsam.courmy.main.entity.area.AreaVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException

class ProfileServerInterestsTest {
    private val source = mockk<ProfileDataSource>()
    private val tokenStore = mockk<TokenStore>(relaxed = true)
    private val cache = mockk<InterestPreferencesDataStore>(relaxed = true)
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        coEvery { source.getMyPage(any(), any()) } returns
            json.decodeFromString<MyPageEnvelope>(
                """
                {
                  "code":200,
                  "data":{
                    "profile":{"id":7,"nickname":"BFF 닉네임","handle":"old","bio":"BFF 소개",
                      "profileImageUrl":"old.png","coursesCnt":12,"followersCnt":23,"followingsCnt":34},
                    "courses":[{"id":"course-1","title":"산책","theme":"DATE"}],
                    "nextCursor":"next-page","hasNext":true
                  }
                }
                """.trimIndent(),
            )
        coEvery { cache.getThemes() } returns listOf("food")
        coEvery { cache.getRegions() } returns listOf(AreaVO("28", "인천광역시", "인천광역시"))
    }

    @Test
    fun `관심사와 편집 필드는 계정 API에서 읽고 BFF 코스와 카운트는 유지한다`() =
        runBlocking {
            coEvery { source.getAccountProfile() } returns
                account(
                    """["CAFETOUR","CULTURE","CAFETOUR","UNKNOWN_THEME"]""",
                    """[{"code":"1168010100","name":"역삼동"}]""",
                )
            val result = repository().getMyProfile(size = 10, cursor = "cursor-input")

            assertEquals(7L, result.id)
            assertEquals("서버 닉네임", result.nickname)
            assertEquals("server_handle", result.handle)
            assertEquals("서버 소개", result.bio)
            assertEquals("new.png", result.profileImageUrl)
            assertEquals(listOf("cafetour", "culture"), result.interestThemes)
            assertEquals(listOf("1168010100"), result.interestRegions.map { it.code })
            assertEquals("서울특별시 강남구 역삼동", result.interestRegions.single().fullName)
            assertEquals(12, result.myCourseCount)
            assertEquals("23", result.followerCount)
            assertEquals("34", result.followingCount)
            assertEquals(listOf("course-1"), result.myCourses.items.map { it.id })
            assertEquals("next-page", result.myCourses.nextCursor)
            assertTrue(result.myCourses.hasNext)
            coVerify(exactly = 1) { source.getMyPage(cursor = "cursor-input", size = 10) }
            coVerify(exactly = 1) { source.getAccountProfile() }
            coVerify(exactly = 0) { cache.getThemes() }
            coVerify(exactly = 0) { cache.getRegions() }
        }

    @Test
    fun `서버 빈 관심사 배열은 예전 기기 캐시로 되살리지 않는다`() =
        runBlocking {
            coEvery { source.getAccountProfile() } returns account("[]", "[]")
            val result = repository().getMyProfile(size = 10, cursor = null)
            assertTrue(result.interestThemes.isEmpty())
            assertTrue(result.interestRegions.isEmpty())
            coVerify(exactly = 0) { cache.getThemes() }
            coVerify(exactly = 0) { cache.getRegions() }
        }

    @Test
    fun `계정 API 실패를 빈 관심사나 로컬 캐시 성공으로 처리하지 않는다`() =
        runBlocking {
            coEvery { source.getAccountProfile() } throws IOException("offline")
            val result = runCatching { repository().getMyProfile(size = 10, cursor = null) }
            assertTrue(result.exceptionOrNull() is IOException)
            coVerify(exactly = 0) { cache.getThemes() }
            coVerify(exactly = 0) { cache.getRegions() }
        }

    @Test
    fun `계정 응답 data 누락도 성공 프로필로 변환하지 않는다`() =
        runBlocking {
            coEvery { source.getAccountProfile() } returns
                json.decodeFromString<AccountProfileEnvelope>("""{"code":200}""")
            val result = runCatching { repository().getMyProfile(size = 10, cursor = null) }
            assertTrue(result.isFailure)
        }

    @Test
    fun `지역 코드가 빠진 서버 항목을 조용히 버리고 일부 관심사만 표시하지 않는다`() =
        runBlocking {
            coEvery { source.getAccountProfile() } returns
                account(
                    """["CAFETOUR"]""",
                    """[{"code":"1168010100","name":"역삼동"},{"name":"코드 누락 지역"}]""",
                )
            val result = runCatching { repository().getMyProfile(size = 10, cursor = null) }
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        }

    private fun account(
        themes: String,
        areas: String,
    ): AccountProfileEnvelope =
        json.decodeFromString(
            """
            {
              "code":200,
              "data":{"id":7,"nickname":"서버 닉네임","handle":"server_handle","bio":"서버 소개",
                "profileImageUrl":"new.png","likeThemes":$themes,"areas":$areas}
            }
            """.trimIndent(),
        )

    private fun repository(): ProfileRepositoryImpl {
        val context = mockk<Context>()
        val assets = mockk<AssetManager>()
        every { context.assets } returns assets
        every { assets.open("legal_areas.json") } answers { File("src/main/assets/legal_areas.json").inputStream() }
        return ProfileRepositoryImpl(source, tokenStore, LegalAreaCodeMapper(context), cache)
    }
}
