package com.chillsam.courmy.main.entity.update

sealed interface AppVersionCheckVO {
    data class Checked(
        val installedVersionCode: Long,
        val latestVersionCode: Long,
        val latestVersionName: String = "",
    ) : AppVersionCheckVO {
        val updateAvailable: Boolean get() = latestVersionCode > installedVersionCode
    }

    /** 네트워크 실패·시간 초과·미설정은 최신 버전으로 간주하지 않는다. */
    data object Unavailable : AppVersionCheckVO
}
