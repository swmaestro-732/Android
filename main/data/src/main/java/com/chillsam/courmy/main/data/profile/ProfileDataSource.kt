package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.profile.dto.FollowEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileEnvelope
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileRequest

class ProfileDataSource(
    private val apiService: ProfileApiService,
) : BaseRemoteDataSource() {
    suspend fun getMyPage(
        cursor: String?,
        size: Int,
    ): MyPageEnvelope = checkResponse(apiService.getMyPage(cursor = cursor, size = size))

    suspend fun getUserPage(
        handle: String,
        cursor: String?,
        size: Int,
    ): MyPageEnvelope = checkResponse(apiService.getUserPage(handle = handle, cursor = cursor, size = size))

    suspend fun follow(userId: Long): FollowEnvelope = checkResponse(apiService.follow(userId))

    suspend fun unfollow(userId: Long): FollowEnvelope = checkResponse(apiService.unfollow(userId))

    suspend fun updateProfile(request: UpdateProfileRequest): UpdateProfileEnvelope =
        checkResponse(apiService.updateProfile(request))
}
