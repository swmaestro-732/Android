package com.chillsam.courmy.main.data.follow

import com.chillsam.courmy.main.data.follow.dto.FollowListEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** 팔로워·팔로잉 목록(공개). 관계 플래그는 요청자(JWT) 기준으로 채워진다. */
interface FollowListApiService {
    @GET("api/v1/users/{userId}/followers")
    suspend fun getFollowers(
        @Path("userId") userId: Long,
        @Query("size") size: Int,
        /** 이전 응답의 `nextCursor`. null 이면 Retrofit 이 파라미터를 뺀다(첫 페이지). */
        @Query("cursor") cursor: String? = null,
    ): Response<FollowListEnvelope>

    @GET("api/v1/users/{userId}/followings")
    suspend fun getFollowings(
        @Path("userId") userId: Long,
        @Query("size") size: Int,
        @Query("cursor") cursor: String? = null,
    ): Response<FollowListEnvelope>
}
