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
    }
