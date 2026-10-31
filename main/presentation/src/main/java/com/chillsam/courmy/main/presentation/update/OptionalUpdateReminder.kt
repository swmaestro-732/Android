package com.chillsam.courmy.main.presentation.update

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject

/** '나중에'를 선택한 기기 현지 날짜에는 앱을 다시 실행해도 안내하지 않는다. */
class OptionalUpdateReminder internal constructor(
    private val preferences: SharedPreferences,
    private val today: () -> LocalDate,
) {
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : this(
        context.getSharedPreferences("optional_app_update", Context.MODE_PRIVATE),
        { LocalDate.now() },
    )

    fun isDismissedToday(): Boolean = preferences.getString(DISMISSED_DATE, null) == today().toString()

    fun dismissForToday() {
        preferences.edit().putString(DISMISSED_DATE, today().toString()).apply()
    }

    private companion object {
        const val DISMISSED_DATE = "dismissed_date"
    }
}
