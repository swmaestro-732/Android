package com.chillsam.courmy.main.data.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding")

/** 온보딩 완료 플래그를 Preferences DataStore 에 보관하는 로컬 데이터소스. */
class OnboardingPreferencesDataStore(
    context: Context,
) {
    private val dataStore = context.onboardingDataStore

    suspend fun isOnboarded(): Boolean = dataStore.data.map { it[KEY_ONBOARDED] ?: false }.first()

    suspend fun setOnboarded() {
        dataStore.edit { it[KEY_ONBOARDED] = true }
    }

    private companion object {
        val KEY_ONBOARDED = booleanPreferencesKey("is_onboarded")
    }
}
