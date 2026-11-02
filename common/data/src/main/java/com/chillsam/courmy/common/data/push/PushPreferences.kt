package com.chillsam.courmy.common.data.push

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushPreferences
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val preferences = context.getSharedPreferences("push_settings", Context.MODE_PRIVATE)

        var enabled: Boolean
            get() = preferences.getBoolean("enabled", true)
            set(value) {
                check(preferences.edit().putBoolean("enabled", value).commit())
            }

        /** 홈 최초 진입 동의 팝업을 처리했는지. */
        var consentDecided: Boolean
            get() = preferences.getBoolean("consent_decided", false)
            set(value) {
                check(preferences.edit().putBoolean("consent_decided", value).commit())
            }

        var marketingAgreed: Boolean
            get() = preferences.getBoolean("marketing_agreed", false)
            set(value) {
                check(preferences.edit().putBoolean("marketing_agreed", value).commit())
            }

        var nightAgreed: Boolean
            get() = preferences.getBoolean("night_agreed", false)
            set(value) {
                check(preferences.edit().putBoolean("night_agreed", value).commit())
            }

        /** 마지막으로 저장한 동의가 서버에 반영됐는지. 결정 전에는 보낼 것이 없어 true 다. */
        var consentSynced: Boolean
            get() = preferences.getBoolean("consent_synced", true)
            set(value) {
                check(preferences.edit().putBoolean("consent_synced", value).commit())
            }
    }
