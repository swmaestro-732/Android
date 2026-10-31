package com.chillsam.courmy.main.data.notification

import com.chillsam.courmy.main.domain.notification.NotificationRepository
import com.chillsam.courmy.main.entity.notification.NotificationPageVO

class NotificationRepositoryImpl(
    private val source: NotificationDataSource,
) : NotificationRepository {
    override suspend fun getNotifications(cursor: Long?): NotificationPageVO {
        require(cursor == null || cursor > 0) { "알림 커서가 올바르지 않습니다." }
        val page = source.getNotifications(cursor).toVO()
        if (cursor != null && page.hasNext) {
            require(requireNotNull(page.nextCursor) < cursor) { "알림 다음 페이지가 진행되지 않습니다." }
        }
        return page
    }

    override suspend fun unreadCount(): Long = source.unreadCount()

    override suspend fun markRead(id: Long) = source.markRead(id)

    override suspend fun markAllRead(): Long = source.markAllRead()
}
