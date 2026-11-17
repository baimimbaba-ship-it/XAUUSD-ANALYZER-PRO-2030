package com.xauusd.pro2030.model

/** A time-bucketed OHLC candle built only from observed provider quotes. */
data class Candle(
    val openTimeMillis: Long,
    val closeTimeMillis: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val tickCount: Int = 0
) {
    val isValid: Boolean
        get() = openTimeMillis < closeTimeMillis &&
            listOf(open, high, low, close).all { it.isFinite() && it > 0.0 } &&
            high >= maxOf(open, close) && low <= minOf(open, close) && low <= high && tickCount >= 0
}

enum class Timeframe(val durationMillis: Long) {
    M1(60_000L), M5(300_000L), M15(900_000L), H1(3_600_000L), H4(14_400_000L), D1(86_400_000L)
}

enum class Direction { BULLISH, BEARISH, NEUTRAL }

data class IndicatorValues(
    val sma: Double? = null,
    val ema: Double? = null,
    val rsi: Double? = null,
    val atr: Double? = null
)

data class SignalScore(
    /** A rule-alignment score, not a probability or expected return. */
    val score: Int,
    val direction: Direction,
    val reasons: List<String>
)
