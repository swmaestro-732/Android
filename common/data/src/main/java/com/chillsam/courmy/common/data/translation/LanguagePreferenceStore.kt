package com.chillsam.courmy.common.data.translation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.domain.translation.LanguageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_settings")

/** 선택 언어를 기기에 보관한다. 저장값이 없으면 한국어(원문) 기준이다. */
@Singleton
class LanguagePreferenceStore
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : LanguageRepository {
        private val dataStore = context.languageDataStore

        override val language: Flow<AppLanguage> =
            dataStore.data.map { preferences -> AppLanguage.fromCode(preferences[KEY_LANGUAGE]) }

        override suspend fun setLanguage(language: AppLanguage) {
            dataStore.edit { it[KEY_LANGUAGE] = language.code }
        }

        private companion object {
            val KEY_LANGUAGE = stringPreferencesKey("app_language")
        }
    }
