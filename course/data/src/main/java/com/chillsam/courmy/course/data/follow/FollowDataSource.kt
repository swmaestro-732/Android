package com.chillsam.courmy.course.data.follow

import com.chillsam.courmy.common.data.BaseRemoteDataSource

class FollowDataSource(
    private val apiService: FollowApiService,
) : BaseRemoteDataSource() {
    suspend fun setFollow(
        userId: Long,
        follow: Boolean,
    ): FollowEnvelope =
        checkResponse(
            if (follow) apiService.follow(userId) else apiService.unfollow(userId),
        )
}
