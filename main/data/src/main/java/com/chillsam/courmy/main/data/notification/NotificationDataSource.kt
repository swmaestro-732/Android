package com.chillsam.courmy.main.data.notification

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import retrofit2.Response

class NotificationDataSource(
    private val api: NotificationApiService,
) : BaseRemoteDataSource() {
    suspend fun getNotifications(cursor: Long?): NotificationPageDTO =
        requireNotNull(checked(api.getNotifications(cursor)).data) { "알림 응답에 data가 없습니다." }

    suspend fun unreadCount(): Long = count(checked(api.unreadCount()))

    suspend fun markRead(id: Long) {
        require(id > 0) { "알림 ID가 올바르지 않습니다." }
        checked(api.markRead(id))
    }

    suspend fun markAllRead(): Long = count(checked(api.markAllRead()))

    private fun count(envelope: NotificationEnvelope<NotificationCountDTO>): Long =
        requireNotNull(envelope.data?.count?.takeIf { it >= 0 }) { "알림 개수가 올바르지 않습니다." }

    private fun <T> checked(response: Response<NotificationEnvelope<T>>): NotificationEnvelope<T> =
        checkResponse(response).also { envelope ->
            check(envelope.code == SUCCESS_CODE) { envelope.message ?: "알림 요청에 실패했습니다." }
        }

    private companion object {
        const val SUCCESS_CODE = 2000
    }
}
