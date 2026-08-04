package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.profile.dto.FollowEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope

class ProfileDataSource(
    private val apiService: ProfileApiService,
) : BaseRemoteDataSource() {
    suspend fun getMyPage(): MyPageEnvelope = checkResponse(apiService.getMyPage())

    suspend fun getUserPage(handle: String): MyPageEnvelope = checkResponse(apiService.getUserPage(handle))

    suspend fun follow(userId: Long): FollowEnvelope = checkResponse(apiService.follow(userId))

    suspend fun unfollow(userId: Long): FollowEnvelope = checkResponse(apiService.unfollow(userId))
}
