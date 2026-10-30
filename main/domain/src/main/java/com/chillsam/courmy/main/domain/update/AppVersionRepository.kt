package com.chillsam.courmy.main.domain.update

import com.chillsam.courmy.main.entity.update.AppVersionCheckVO

interface AppVersionRepository {
    suspend fun checkVersion(forceRefresh: Boolean = false): AppVersionCheckVO
}
