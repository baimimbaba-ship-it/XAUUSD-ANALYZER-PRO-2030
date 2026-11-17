package com.xauusd.pro2030.data

data class SignalHistory(
    val id: Long = 0L,
    val symbol: String = "XAUUSD",
    val signalType: String = "WAIT",
    /** Rule-strength index, not a probability or expected return. */
    val score: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val timeframe: String = "1m",
    val source: String = "BiQuote sampled snapshots",
    val lastPrice: Double = 0.0,
    val sma: Double? = null,
    val ema: Double? = null,
    val rsi: Double? = null,
    val reason: String = "",
    val freshness: String = "UNKNOWN"
)
