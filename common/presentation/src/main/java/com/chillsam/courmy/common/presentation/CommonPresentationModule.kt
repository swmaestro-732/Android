package com.chillsam.courmy.common.presentation

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import com.chillsam.courmy.common.domain.coroutine.IoDispatcher
import com.chillsam.courmy.common.domain.helper.MessageHelper
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.helper.ResourceHelper
import com.chillsam.courmy.common.presentation.helper.MessageHelperImpl
import com.chillsam.courmy.common.presentation.helper.NavigationHelperImpl
import com.chillsam.courmy.common.presentation.helper.ResourceHelperImpl
import com.chillsam.courmy.tti.DebugTTILogger
import com.chillsam.courmy.tti.NoOpTTIReporter
import com.chillsam.courmy.tti.RemoteTTILogger
import com.chillsam.courmy.tti.TTIHelper
import com.chillsam.courmy.tti.TTIHelperImpl
import com.chillsam.courmy.tti.TTILogger
import com.chillsam.courmy.tti.TTIReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonPresentationModule {
    @Provides
    @Singleton
    fun provideMessageHelper(@ApplicationContext context: Context): MessageHelper =
        MessageHelperImpl(context)

    @Provides
    @Singleton
    fun provideNavigationHelper(): NavigationHelper = NavigationHelperImpl()

    @Provides
    @Singleton
    fun provideResourceHelper(@ApplicationContext context: Context): ResourceHelper =
        ResourceHelperImpl(context)

    @Provides
    @Singleton
    fun provideTTILogger(): TTILogger {
        return if (BuildConfig.DEBUG) DebugTTILogger() else RemoteTTILogger()
    }

    @Provides
    @Singleton
    fun provideTTIReporter(): TTIReporter = NoOpTTIReporter

    @Provides
    @Singleton
    fun provideTTIHelper(
        reporter: TTIReporter,
        logger: TTILogger,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): TTIHelper = TTIHelperImpl(
        reporter = reporter,
        logger = logger,
        dispatcher = ioDispatcher,
    )
}

val LocalTTIHelper = compositionLocalOf<TTIHelper> {
    error("LocalTTIHelper is not provided. Wrap with CompositionLocalProvider in MainActivity.")
}