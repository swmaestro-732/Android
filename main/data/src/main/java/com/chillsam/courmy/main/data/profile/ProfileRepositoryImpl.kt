package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageScreenDTO
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileRequest
import com.chillsam.courmy.main.data.profile.dto.formatCount
import com.chillsam.courmy.main.data.profile.dto.toMyProfileVO
import com.chillsam.courmy.main.data.profile.dto.toUserProfileVO
import com.chillsam.courmy.main.domain.profile.ProfileRepository
import com.chillsam.courmy.main.entity.area.AreaVO
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
    private val interestStore: InterestPreferencesDataStore,
) : ProfileRepository {
    /**
     * 소개(bio)는 응답에 담겨 오므로 그대로 쓰고, 관심 테마·지역만 기기 캐시에서 덮어쓴다.
     *
     * TODO-API-SPEC: 관심사는 서버가 저장은 하지만 마이페이지 응답(`MyPageProfileResponse`)에
     * `likeThemes`·`areas` 가 없어 되읽을 수 없다. 조회 경로가 생기면 이 병합과
     * [interestStore] 를 함께 지운다. [wiki-needed]
     */
    override suspend fun getMyProfile(
        size: Int,
        cursor: String?,
    ): MyProfileVO =
        dataSource
            .getMyPage(cursor = cursor, size = size)
            .requireData()
            .toMyProfileVO()
            .copy(
                interestThemes = interestStore.getThemes(),
                interestRegions = interestStore.getRegions(),
            )

    /**
     * 서버에 먼저 반영하고 성공했을 때만 캐시를 갱신한다 — 실패했는데 화면만 바뀌면
     * 저장된 것처럼 보이다가 다음 실행에서 조용히 되돌아간다.
     */
    override suspend fun saveInterestThemes(themes: List<String>) {
        dataSource.updateProfile(UpdateProfileRequest(likeThemes = themes))
        interestStore.setThemes(themes)
    }

    /** 서버에는 코드만 보낸다. 지역 이름은 화면 표시용이라 캐시에만 남는다. */
    override suspend fun saveInterestRegions(regions: List<AreaVO>) {
        dataSource.updateProfile(UpdateProfileRequest(areaCodes = regions.map { it.code }))
        interestStore.setRegions(regions)
    }

    override suspend fun cacheInterests(
        themes: List<String>,
        regions: List<AreaVO>,
    ) {
        interestStore.setThemes(themes)
        interestStore.setRegions(regions)
    }

    override suspend fun getUserProfile(
        handle: String,
        size: Int,
        cursor: String?,
    ): UserProfileVO =
        dataSource
            .getUserPage(handle = handle, cursor = cursor, size = size)
            .requireData()
            .toUserProfileVO(myUserId = tokenStore.userId)

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
     *
     * [bio] 만 예외로 빈 문자열을 그대로 보낸다 — 소개는 지울 수 있어야 하고 서버도 길이 제약 없이 받는다.
     */
    override suspend fun updateProfile(
        nickname: String?,
        handle: String?,
        profileImageUrl: String?,
        bio: String?,
    ) {
        dataSource.updateProfile(
            UpdateProfileRequest(
                nickname = nickname?.takeIf { it.isNotBlank() },
                handle = handle?.takeIf { it.isNotBlank() },
                profileImageUrl = profileImageUrl?.takeIf { it.isNotBlank() },
                bio = bio,
            ),
        )
    }

    private fun MyPageEnvelope.requireData(): MyPageScreenDTO =
        requireNotNull(data) { message ?: "마이페이지 응답에 data 가 없습니다." }
}
