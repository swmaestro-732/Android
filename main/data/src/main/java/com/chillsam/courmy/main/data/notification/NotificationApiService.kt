package com.chillsam.courmy.main.data.notification

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {
    @GET("api/v1/my/notifications")
    suspend fun getNotifications(
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int = 20,
    ): Response<NotificationEnvelope<NotificationPageDTO>>

    @GET("api/v1/my/notifications/unread-count")
    suspend fun unreadCount(): Response<NotificationEnvelope<NotificationCountDTO>>

    @PATCH("api/v1/my/notifications/{id}/read")
    suspend fun markRead(
        @Path("id") id: Long,
    ): Response<NotificationEnvelope<NotificationCountDTO>>

    @POST("api/v1/my/notifications/read-all")
    suspend fun markAllRead(): Response<NotificationEnvelope<NotificationCountDTO>>
}
