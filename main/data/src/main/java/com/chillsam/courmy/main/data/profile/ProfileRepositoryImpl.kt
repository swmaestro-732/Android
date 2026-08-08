package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageScreenDTO
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileRequest
import com.chillsam.courmy.main.data.profile.dto.formatCount
import com.chillsam.courmy.main.data.profile.dto.toMyProfileVO
import com.chillsam.courmy.main.data.profile.dto.toUserProfileVO
import com.chillsam.courmy.main.domain.profile.ProfileRepository
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.entity.user.FollowResultVO
import com.chillsam.courmy.main.entity.user.UserProfileVO

/**
 * [TokenStore] 는 "이 프로필이 나인지" 판정에만 쓴다(요청에는 실지 않는다 — 서버가 JWT 로 식별).
 * 자기 자신을 연 경우 화면에서 팔로우 버튼을 숨기기 위한 값이다.
 */
class ProfileRepositoryImpl(
    private val dataSource: ProfileDataSource,
    private val tokenStore: TokenStore,
    private val bioStore: BioPreferencesDataStore,
) : ProfileRepository {
    /**
     * 소개(bio)만 로컬 저장소에서 덮어쓴다.
     *
     * TODO-API-SPEC: 응답(`MyPageProfileResponse`)에 소개 필드가 없어 여기서 합친다.
     * 서버가 내려주기 시작하면 이 병합과 [bioStore] 를 지우고 응답 값을 그대로 쓴다. [wiki-needed]
     */
    override suspend fun getMyProfile(): MyProfileVO =
        dataSource
            .getMyPage()
            .requireData()
            .toMyProfileVO()
            .copy(bio = bioStore.getBio(requireUserId()))

    override suspend fun saveBio(bio: String) = bioStore.setBio(requireUserId(), bio)

    override suspend fun getUserProfile(handle: String): UserProfileVO =
        dataSource.getUserPage(handle).requireData().toUserProfileVO(myUserId = tokenStore.userId)

    override suspend fun setFollow(
        userId: Long,
        follow: Boolean,
    ): FollowResultVO {
        val envelope = if (follow) dataSource.follow(userId) else dataSource.unfollow(userId)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "팔로우 응답에 data 가 없습니다."
            }
        return FollowResultVO(
            isFollowing = data.isFollowing,
            followerCount = formatCount(data.followersCnt ?: 0),
        )
    }

    /**
     * 공백만 있는 값은 서버가 거부하므로(`@Size(min=1)`) null 로 바꿔 "변경 안 함"으로 보낸다.
     * 대상은 서버가 JWT 로 식별하므로 userId 를 싣지 않는다.
     */
    override suspend fun updateProfile(
        nickname: String?,
        handle: String?,
        profileImageUrl: String?,
    ) {
        dataSource.updateProfile(
            UpdateProfileRequest(
                nickname = nickname?.takeIf { it.isNotBlank() },
                handle = handle?.takeIf { it.isNotBlank() },
                profileImageUrl = profileImageUrl?.takeIf { it.isNotBlank() },
            ),
        )
    }

    private fun MyPageEnvelope.requireData(): MyPageScreenDTO =
        requireNotNull(data) { message ?: "마이페이지 응답에 data 가 없습니다." }

    private fun requireUserId(): Long = requireNotNull(tokenStore.userId) { "로그인한 사용자 ID가 없습니다." }
}
