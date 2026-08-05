package com.chillsam.courmy.main.data.follow

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.data.follow.dto.FollowListEnvelope
import com.chillsam.courmy.main.data.follow.dto.toVOList
import com.chillsam.courmy.main.domain.follow.FollowListRepository
import com.chillsam.courmy.main.entity.my.FollowUserVO

/**
 * 엔드포인트가 대상 사용자 id 를 경로로 받으므로, "내" 목록은 세션 토큰에서 id 를 꺼내 채운다
 * (프로필의 자기 자신 판정과 같은 방식).
 */
class FollowListRepositoryImpl(
    private val dataSource: FollowListDataSource,
    private val tokenStore: TokenStore,
) : FollowListRepository {
    override suspend fun getMyFollowers(size: Int): List<FollowUserVO> =
        dataSource.getFollowers(requireMyUserId(), size).requireData()

    override suspend fun getMyFollowings(size: Int): List<FollowUserVO> =
        dataSource.getFollowings(requireMyUserId(), size).requireData()

    private fun requireMyUserId(): Long =
        requireNotNull(tokenStore.userId) {
            "팔로우 목록 조회에 필요한 사용자 id 가 없습니다(세션 없음/토큰 파싱 실패)."
        }

    private fun FollowListEnvelope.requireData(): List<FollowUserVO> =
        requireNotNull(data) { message ?: "팔로우 목록 응답에 data 가 없습니다." }.toVOList()
}
