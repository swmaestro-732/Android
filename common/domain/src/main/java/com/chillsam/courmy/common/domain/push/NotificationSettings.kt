package com.chillsam.courmy.common.domain.push

/** 이 설치의 전체 푸시 수신 설정. OS 권한과 서버 알림함은 별개다. */
interface NotificationSettings {
    val notificationsEnabled: Boolean

    suspend fun setNotificationsEnabled(enabled: Boolean)
}
