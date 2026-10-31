package com.chillsam.courmy.common.domain.push

/** 푸시 data 경계에서 허용한 알림 종류/대상 조합과 양수 ID만 받아들인다. */
data class PushTarget(
    val type: String,
    val targetType: String,
    val targetId: Long,
    val notificationId: Long,
) {
    companion object {
        private val courseTypes =
            setOf(
                "COURSE_LIKED",
                "COURSE_COMMENTED",
                "COURSE_TRACE_COMPLETED",
                "COURSE_REVIEWED",
                "COURSE_BOOKMARKED",
            )

        fun parse(data: Map<String, String>): PushTarget? {
            val type = data["type"].orEmpty()
            val targetType = data["targetType"].orEmpty()
            val supported =
                (type in courseTypes && targetType == "COURSE") ||
                    (type == "USER_FOLLOWED" && targetType == "USER")
            if (!supported) return null
            val targetId = data["targetId"]?.toLongOrNull()?.takeIf { it > 0 }
            val notificationId = data["notificationId"]?.toLongOrNull()?.takeIf { it > 0 }
            return if (targetId != null && notificationId != null) {
                PushTarget(type, targetType, targetId, notificationId)
            } else {
                null
            }
        }
    }
}
