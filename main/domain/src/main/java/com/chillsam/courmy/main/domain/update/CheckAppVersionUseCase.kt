package com.chillsam.courmy.main.domain.update

import com.chillsam.courmy.main.entity.update.AppVersionCheckVO
import javax.inject.Inject

class CheckAppVersionUseCase
    @Inject
    constructor(
        private val repository: AppVersionRepository,
    ) {
        suspend operator fun invoke(forceRefresh: Boolean = false): AppVersionCheckVO =
            repository.checkVersion(forceRefresh)
    }
