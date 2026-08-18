package com.chillsam.courmy.main.data.follow

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.follow.dto.FollowListEnvelope

class FollowListDataSource(
    private val apiService: FollowListApiService,
) : BaseRemoteDataSource() {
    suspend fun getFollowers(
        userId: Long,
        size: Int,
        cursor: String?,
    ): FollowListEnvelope = checkResponse(apiService.getFollowers(userId, size, cursor))

    suspend fun getFollowings(
        userId: Long,
        size: Int,
        cursor: String?,
    ): FollowListEnvelope = checkResponse(apiService.getFollowings(userId, size, cursor))
}
