package com.chillsam.courmy.main.entity.notification

/** actorCount는 고유 사용자 수가 아닌 묶인 활동 횟수다. */
data class NotificationItemVO(
    val id: Long = 0,
    val type: String = "",
    val targetType: String = "",
    val targetId: Long = 0,
    val actorCount: Int = 1,
    val lastActorId: Long = 0,
    val actorNickname: String = "",
    val actorProfileImageUrl: String? = null,
    val courseTitle: String? = null,
    val isRead: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = "",
)

data class NotificationPageVO(
    val items: List<NotificationItemVO> = emptyList(),
    val hasNext: Boolean = false,
    val nextCursor: Long? = null,
)
