package com.xauusd.pro2030.core.settings

import android.content.Context

/** User-selectable dashboard preferences with bounded, safe defaults. */
class UserPreferences(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var refreshIntervalSeconds: Long
        get() = preferences.getLong(KEY_REFRESH_SECONDS, DEFAULT_REFRESH_SECONDS).coerceIn(MIN_REFRESH_SECONDS, MAX_REFRESH_SECONDS)
        set(value) {
            require(value in MIN_REFRESH_SECONDS..MAX_REFRESH_SECONDS) { "Refresh interval must be between 15 and 300 seconds" }
            preferences.edit().putLong(KEY_REFRESH_SECONDS, value).apply()
        }

    var timeframeMillis: Long
        get() = preferences.getLong(KEY_TIMEFRAME_MILLIS, DEFAULT_TIMEFRAME_MILLIS).coerceIn(MIN_TIMEFRAME_MILLIS, MAX_TIMEFRAME_MILLIS)
        set(value) {
            require(value in MIN_TIMEFRAME_MILLIS..MAX_TIMEFRAME_MILLIS) { "Timeframe must be between one and sixty minutes" }
            preferences.edit().putLong(KEY_TIMEFRAME_MILLIS, value).apply()
        }

    fun clear() {
        preferences.edit().clear().apply()
    }

    companion object {
        const val DEFAULT_REFRESH_SECONDS = 15L
        const val MIN_REFRESH_SECONDS = 15L
        const val MAX_REFRESH_SECONDS = 300L
        const val DEFAULT_TIMEFRAME_MILLIS = 60_000L
        const val MIN_TIMEFRAME_MILLIS = 60_000L
        const val MAX_TIMEFRAME_MILLIS = 3_600_000L
        private const val PREFERENCES_NAME = "user_preferences_v1"
        private const val KEY_REFRESH_SECONDS = "refresh_interval_seconds"
        private const val KEY_TIMEFRAME_MILLIS = "timeframe_millis"
    }
}
