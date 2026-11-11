package com.chillsam.courmy.common.data.translation

import com.chillsam.courmy.common.domain.translation.LanguageRepository
import com.chillsam.courmy.common.domain.translation.Translator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 온디바이스 번역 바인딩. 선택 언어 저장소와 ML Kit 번역기를 각 인터페이스에 연결한다. */
@Module
@InstallIn(SingletonComponent::class)
abstract class TranslationModule {
    @Binds
    @Singleton
    abstract fun bindTranslator(impl: MlKitTranslator): Translator

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(impl: LanguagePreferenceStore): LanguageRepository
}
