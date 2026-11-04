package com.chillsam.courmy.main.data.notification

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    // 알림 하나 읽음. `PATCH /{id}` + body {read:true}. 응답 data 없음(멱등).
    @PATCH("api/v1/my/notifications/{id}")
    suspend fun markRead(
        @Path("id") id: Long,
        @Body request: NotificationReadRequest,
    ): Response<NotificationEnvelope<NotificationCountDTO>>

    // 전체 읽음. 컬렉션에 `PATCH` + body {read:true}. 응답에 바뀐 개수.
    @PATCH("api/v1/my/notifications")
    suspend fun markAllRead(
        @Body request: NotificationReadRequest,
    ): Response<NotificationEnvelope<NotificationCountDTO>>
}
