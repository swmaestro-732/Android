package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.profile.dto.AccountProfileEnvelope
import com.chillsam.courmy.main.data.profile.dto.AvailabilityEnvelope
import com.chillsam.courmy.main.data.profile.dto.FollowEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileRequest

class ProfileDataSource(
    private val apiService: ProfileApiService,
) : BaseRemoteDataSource() {
    suspend fun getMyPage(): MyPageEnvelope = checkResponse(apiService.getMyPage())

    suspend fun getUserPage(handle: String): MyPageEnvelope = checkResponse(apiService.getUserPage(handle))

    suspend fun updateProfile(body: UpdateProfileRequest): AccountProfileEnvelope =
        checkResponse(apiService.updateProfile(body))

    suspend fun checkHandleAvailability(handle: String): AvailabilityEnvelope =
        checkResponse(apiService.checkHandleAvailability(handle))

    suspend fun follow(userId: Long): FollowEnvelope = checkResponse(apiService.follow(userId))

    suspend fun unfollow(userId: Long): FollowEnvelope = checkResponse(apiService.unfollow(userId))
}
