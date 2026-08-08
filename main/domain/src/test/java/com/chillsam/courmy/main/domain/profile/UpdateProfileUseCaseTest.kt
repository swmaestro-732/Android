package com.chillsam.courmy.main.domain.profile

import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.common.domain.media.UploadPurpose
import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.entity.user.FollowResultVO
import com.chillsam.courmy.main.entity.user.UserProfileVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 프로필 수정 순서 검증.
 * 서버는 이미지 URL 문자열만 받으므로 업로드가 **먼저** 끝나야 하고,
 * 업로드가 실패하면 프로필도 갱신하지 않아야 한다(반쯤 반영된 상태 방지).
 */
class UpdateProfileUseCaseTest {
    private class FakeProfileRepository : ProfileRepository {
        var updatedImageUrl: String? = null
            private set
        var updateCalled = false
            private set
        var savedBio: String? = null
            private set

        override suspend fun getMyProfile(): MyProfileVO = error("호출되지 않아야 한다")

        override suspend fun saveBio(bio: String) {
            savedBio = bio
        }

        override suspend fun saveInterestThemes(themes: List<String>) = error("호출되지 않아야 한다")

        override suspend fun saveInterestRegions(regions: List<AreaVO>) = error("호출되지 않아야 한다")

        override suspend fun getUserProfile(handle: String): UserProfileVO = error("호출되지 않아야 한다")

        override suspend fun setFollow(
            userId: Long,
            follow: Boolean,
        ): FollowResultVO = error("호출되지 않아야 한다")

        override suspend fun updateProfile(
            nickname: String?,
            handle: String?,
            profileImageUrl: String?,
        ) {
            updateCalled = true
            updatedImageUrl = profileImageUrl
        }
    }

    private class FakeMediaRepository(
        private val failing: Boolean = false,
    ) : MediaRepository {
        var uploadedUri: String? = null
            private set

        override suspend fun uploadImage(
            localUri: String,
            purpose: UploadPurpose,
        ): String {
            uploadedUri = localUri
            if (failing) error("upload failed")
            return "https://cdn.example.com/uploaded.jpg"
        }
    }

    @Test
    fun `이미지를 고르지 않으면 업로드하지 않는다`() =
        runTest {
            val profile = FakeProfileRepository()
            val media = FakeMediaRepository()

            UpdateProfileUseCase(profile, media)(nickname = "지호")

            assertNull(media.uploadedUri)
            assertTrue(profile.updateCalled)
            assertNull(profile.updatedImageUrl)
        }

    /** 공백만 있는 값은 사용자가 고른 이미지가 아니라 빈 입력이므로 업로드 대상이 아니다. */
    @Test
    fun `공백뿐인 uri 는 업로드하지 않는다`() =
        runTest {
            val media = FakeMediaRepository()

            UpdateProfileUseCase(FakeProfileRepository(), media)(localImageUri = "   ")

            assertNull(media.uploadedUri)
        }

    @Test
    fun `이미지를 고르면 업로드한 URL 로 수정한다`() =
        runTest {
            val profile = FakeProfileRepository()
            val media = FakeMediaRepository()

            UpdateProfileUseCase(profile, media)(localImageUri = "content://pick/1")

            assertEquals("content://pick/1", media.uploadedUri)
            assertEquals("https://cdn.example.com/uploaded.jpg", profile.updatedImageUrl)
        }

    @Test
    fun `업로드가 실패하면 프로필을 갱신하지 않는다`() =
        runTest {
            val profile = FakeProfileRepository()

            runCatching {
                UpdateProfileUseCase(profile, FakeMediaRepository(failing = true))(localImageUri = "content://pick/1")
            }

            assertTrue("업로드 실패 시 수정 요청이 나가면 안 된다", !profile.updateCalled)
        }

    @Test
    fun `소개를 바꾸면 로컬에 저장된다`() =
        runTest {
            val repository = FakeProfileRepository()
            val useCase = UpdateProfileUseCase(repository, FakeMediaRepository())

            useCase(bio = "성수 산책 좋아해요")

            assertEquals("성수 산책 좋아해요", repository.savedBio)
            assertTrue("소개만 바꿀 때 빈 서버 요청을 보내면 안 된다", !repository.updateCalled)
        }

    @Test
    fun `소개를 건드리지 않으면 저장하지 않는다`() =
        runTest {
            val repository = FakeProfileRepository()
            val useCase = UpdateProfileUseCase(repository, FakeMediaRepository())

            useCase(nickname = "허나영임")

            assertNull(repository.savedBio)
        }
}
