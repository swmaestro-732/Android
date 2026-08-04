package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageScreenDTO
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
) : ProfileRepository {
    override suspend fun getMyProfile(): MyProfileVO = dataSource.getMyPage().requireData().toMyProfileVO()

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

    private fun MyPageEnvelope.requireData(): MyPageScreenDTO =
        requireNotNull(data) { message ?: "마이페이지 응답에 data 가 없습니다." }
}
