package com.chillsam.courmy.main.data.notification

import com.chillsam.courmy.main.entity.notification.NotificationItemVO
import com.chillsam.courmy.main.entity.notification.NotificationPageVO
import kotlinx.serialization.Serializable

@Serializable
data class NotificationEnvelope<T>(
    val code: Int? = null,
    val message: String? = null,
    val data: T? = null,
)

@Serializable
data class NotificationCountDTO(
    val count: Long? = null,
)

@Serializable
data class NotificationItemDTO(
    val id: Long? = null,
    val type: String? = null,
    val targetType: String? = null,
    val targetId: Long? = null,
    val actorCount: Int? = null,
    val lastActorId: Long? = null,
    val actorNickname: String? = null,
    val actorProfileImageUrl: String? = null,
    val courseTitle: String? = null,
    val isRead: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
) {
    fun toVO(): NotificationItemVO =
        NotificationItemVO(
            id = requireNotNull(id?.takeIf { it > 0 }) { "알림 ID가 올바르지 않습니다." },
            type = requireNotNull(type?.takeIf { it.isNotBlank() }) { "알림 유형이 없습니다." },
            targetType = requireNotNull(targetType?.takeIf { it.isNotBlank() }) { "알림 대상 유형이 없습니다." },
            targetId = requireNotNull(targetId?.takeIf { it > 0 }) { "알림 대상 ID가 올바르지 않습니다." },
            actorCount = actorCount?.coerceAtLeast(1) ?: 1,
            lastActorId = requireNotNull(lastActorId?.takeIf { it > 0 }) { "알림 작성자 ID가 올바르지 않습니다." },
            actorNickname = actorNickname.orEmpty(),
            actorProfileImageUrl = actorProfileImageUrl?.takeIf { it.isNotBlank() },
            courseTitle = courseTitle?.takeIf { it.isNotBlank() },
            isRead = isRead ?: false,
            createdAt = createdAt.orEmpty(),
            updatedAt = updatedAt.orEmpty(),
        )
}

@Serializable
data class NotificationPageDTO(
    val items: List<NotificationItemDTO>? = null,
    val hasNext: Boolean? = null,
    val nextCursor: Long? = null,
) {
    fun toVO(): NotificationPageVO {
        val mapped = requireNotNull(items) { "알림 목록이 없습니다." }.map(NotificationItemDTO::toVO)
        val more = requireNotNull(hasNext) { "알림 페이지 정보가 없습니다." }
        val cursor = nextCursor?.takeIf { it > 0 }
        require(!more || (cursor != null && mapped.isNotEmpty())) { "알림 다음 페이지 정보가 올바르지 않습니다." }
        return NotificationPageVO(mapped, more, cursor.takeIf { more })
    }
}
