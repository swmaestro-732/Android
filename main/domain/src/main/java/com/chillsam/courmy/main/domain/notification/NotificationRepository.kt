package com.chillsam.courmy.main.domain.notification

import com.chillsam.courmy.main.entity.notification.NotificationPageVO

interface NotificationRepository {
    suspend fun getNotifications(cursor: Long? = null): NotificationPageVO

    suspend fun unreadCount(): Long

    suspend fun markRead(id: Long)

    /** 새로 읽음 처리한 행 수. 남은 미확인 알림 수가 아니다. */
    suspend fun markAllRead(): Long
}
