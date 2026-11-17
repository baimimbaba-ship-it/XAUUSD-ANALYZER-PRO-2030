package com.xauusd.pro2030.core.engine

import kotlin.math.abs

/** Technical indicators over ordered closing prices. No values are returned until enough samples exist. */
class IndicatorEngine {
    data class Indicators(
        val sma: Double,
        val ema: Double,
        val rsi: Double,
        val period: Int
    )

    fun calculate(closes: List<Double>, period: Int = DEFAULT_PERIOD): Indicators? {
        require(period > 0) { "Period must be positive" }
        require(closes.all { it.isFinite() && it > 0.0 }) { "Closing prices must be finite and positive" }
        if (closes.size < period + 1) return null

        val recent = closes.takeLast(period)
        val sma = recent.average()
        val alpha = 2.0 / (period + 1.0)
        var ema = closes.first()
        closes.drop(1).forEach { close -> ema = close * alpha + ema * (1.0 - alpha) }

        var gains = 0.0
        var losses = 0.0
        for (index in closes.size - period until closes.size) {
            val change = closes[index] - closes[index - 1]
            if (change > 0.0) gains += change else losses += abs(change)
        }
        val averageGain = gains / period
        val averageLoss = losses / period
        val rsi = when {
            averageLoss == 0.0 && averageGain == 0.0 -> 50.0
            averageLoss == 0.0 -> 100.0
            else -> 100.0 - (100.0 / (1.0 + averageGain / averageLoss))
        }
        return Indicators(sma, ema, rsi, period)
    }

    companion object {
        const val DEFAULT_PERIOD = 14
    }
}
