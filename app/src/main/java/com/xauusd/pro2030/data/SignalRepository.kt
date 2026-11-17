package com.xauusd.pro2030.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Small bounded on-device history store. Stored privately in app SharedPreferences. */
class SignalRepository(context: Context, private val maxEntries: Int = DEFAULT_MAX_ENTRIES) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    init {
        require(maxEntries > 0) { "History capacity must be positive" }
    }

    @Synchronized
    fun saveHistory(signal: SignalHistory) {
        val entries = readEntries().toMutableList()
        val normalized = signal.copy(id = signal.id.takeIf { it > 0L } ?: signal.timestamp)
        entries.removeAll { it.id == normalized.id }
        entries.add(0, normalized)
        preferences.edit().putString(KEY_HISTORY, JSONArray().apply {
            entries.take(maxEntries).forEach { put(it.toJson()) }
        }.toString()).apply()
    }

    @Synchronized
    fun getHistory(): List<SignalHistory> = readEntries()

    @Synchronized
    fun clearHistory() {
        preferences.edit().remove(KEY_HISTORY).apply()
    }

    private fun readEntries(): List<SignalHistory> {
        val encoded = preferences.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val json = JSONArray(encoded)
            (0 until json.length()).mapNotNull { index ->
                json.optJSONObject(index)?.toSignalHistory()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun SignalHistory.toJson() = JSONObject().apply {
        put("id", id)
        put("symbol", symbol)
        put("signalType", signalType)
        put("score", score)
        put("timestamp", timestamp)
        put("timeframe", timeframe)
        put("source", source)
        put("lastPrice", lastPrice)
        put("sma", sma ?: JSONObject.NULL)
        put("ema", ema ?: JSONObject.NULL)
        put("rsi", rsi ?: JSONObject.NULL)
        put("reason", reason)
        put("freshness", freshness)
    }

    private fun JSONObject.toSignalHistory() = SignalHistory(
        id = optLong("id"),
        symbol = optString("symbol", "XAUUSD"),
        signalType = optString("signalType", "WAIT"),
        score = optDouble("score", 0.0),
        timestamp = optLong("timestamp"),
        timeframe = optString("timeframe", "1m"),
        source = optString("source", "unknown"),
        lastPrice = optDouble("lastPrice", 0.0),
        sma = optNullableDouble("sma"),
        ema = optNullableDouble("ema"),
        rsi = optNullableDouble("rsi"),
        reason = optString("reason", ""),
        freshness = optString("freshness", "UNKNOWN")
    )

    private fun JSONObject.optNullableDouble(key: String): Double? =
        if (!has(key) || isNull(key)) null else optDouble(key).takeIf { it.isFinite() }

    companion object {
        private const val PREFERENCES_NAME = "signal_history_v1"
        private const val KEY_HISTORY = "entries"
        const val DEFAULT_MAX_ENTRIES = 500
    }
}
