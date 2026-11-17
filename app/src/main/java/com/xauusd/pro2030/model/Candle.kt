package com.xauusd.pro2030.model

data class Candle(
    val openTimeMillis: Long,
    val closeTimeMillis: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val sampleCount: Int
) {
    init {
        require(openTimeMillis >= 0L) { "Candle open time must be non-negative" }
        require(closeTimeMillis > openTimeMillis) { "Candle close time must be after its open time" }
        require(listOf(open, high, low, close).all { it.isFinite() && it > 0.0 }) { "Candle prices must be finite and positive" }
        require(high >= maxOf(open, close, low)) { "Candle high is inconsistent" }
        require(low <= minOf(open, close, high)) { "Candle low is inconsistent" }
        require(sampleCount > 0) { "Candle must contain at least one sample" }
    }
}
