package com.chillsam.courmy.main.data.update

import com.chillsam.courmy.main.domain.update.AppVersionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppVersionDataModule {
    @Binds
    abstract fun bindAppVersionRepository(repository: RemoteConfigAppVersionRepository): AppVersionRepository
}
