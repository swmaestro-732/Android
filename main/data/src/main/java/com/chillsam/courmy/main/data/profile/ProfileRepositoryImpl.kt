package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.entity.format.formatCount
import com.chillsam.courmy.main.data.area.LegalAreaCodeMapper
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageScreenDTO
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileRequest
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
    private val areaCodeMapper: LegalAreaCodeMapper,
    private val interestStore: InterestPreferencesDataStore,
) : ProfileRepository {
    /** 코스·카운트는 BFF, 편집 필드·관심사는 계정 API의 서버 값을 사용한다. */
    override suspend fun getMyProfile(
        size: Int,
        cursor: String?,
    ): MyProfileVO {
        val page = dataSource.getMyPage(cursor = cursor, size = size).requireData().toMyProfileVO()
        val account =
            dataSource.getAccountProfile().let {
                requireNotNull(it.data) { it.message ?: "내 프로필 응답에 data 가 없습니다." }
            }
        return page.copy(
            id = account.id ?: page.id,
            nickname = account.nickname.orEmpty(),
            handle = account.handle.orEmpty(),
            bio = account.bio.orEmpty(),
            profileImageUrl = account.profileImageUrl.orEmpty(),
            interestThemes =
                account.likeThemes
                    .orEmpty()
                    .mapNotNull { CourseTagVO.fromCode(it)?.code }
                    .distinct(),
            interestRegions = areaCodeMapper.restore(account.areas.orEmpty().map { it.toVO() }),
        )
    }

    /**
     * 서버에 먼저 반영하고 성공했을 때만 캐시를 갱신한다 — 실패했는데 화면만 바뀌면
     * 저장된 것처럼 보이다가 다음 실행에서 조용히 되돌아간다.
     */
    override suspend fun saveInterestThemes(themes: List<String>) {
        val tags = themes.map { requireNotNull(CourseTagVO.fromCode(it)) { "알 수 없는 관심 테마: $it" } }.distinct()
        // 서버는 CourseCategory enum 이름(대문자)으로 검증한다. 기기 캐시는 소문자 코드로 통일.
        dataSource.updateProfile(UpdateProfileRequest(likeThemes = tags.map { it.name }))
        interestStore.setThemes(tags.map { it.code })
    }

    /** 서버에는 법정 읍·면·동 코드 배열, 캐시에는 화면의 압축된 선택과 이름을 보관한다. */
    override suspend fun saveInterestRegions(regions: List<AreaVO>) {
        dataSource.updateProfile(UpdateProfileRequest(areaCodes = areaCodeMapper.expand(regions.map { it.code })))
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
