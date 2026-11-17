package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import com.xauusd.pro2030.model.IndicatorValues
import kotlin.math.abs
import kotlin.math.max

/** Pure indicator calculations. A null result means the input history is insufficient. */
class IndicatorEngine {
    fun calculateIndicators(candles: List<Candle>, period: Int = 14): IndicatorValues {
        require(period > 0) { "period must be positive" }
        val valid = candles.filter(Candle::isValid)
        val closes = valid.map { it.close }
        val sma = closes.takeIf { it.size >= period }?.takeLast(period)?.average()
        val ema = ema(closes, period)
        val rsi = rsi(closes, period)
        val atr = if (valid.size > period) {
            val ranges = valid.zipWithNext().map { (previous, current) ->
                max(current.high - current.low, max(abs(current.high - previous.close), abs(current.low - previous.close)))
            }
            ranges.takeLast(period).takeIf { it.size == period }?.average()
        } else null
        return IndicatorValues(sma = sma, ema = ema, rsi = rsi, atr = atr)
    }

    private fun ema(values: List<Double>, period: Int): Double? {
        if (values.size < period) return null
        val alpha = 2.0 / (period + 1.0)
        return values.drop(period).fold(values.take(period).average()) { result, value -> alpha * value + (1.0 - alpha) * result }
    }

    private fun rsi(closes: List<Double>, period: Int): Double? {
        if (closes.size <= period) return null
        val changes = closes.zipWithNext { a, b -> b - a }.takeLast(period)
        val gains = changes.filter { it > 0.0 }.sum() / period
        val losses = changes.filter { it < 0.0 }.sumOf { -it } / period
        return when {
            losses == 0.0 && gains == 0.0 -> 50.0
            losses == 0.0 -> 100.0
            else -> 100.0 - 100.0 / (1.0 + gains / losses)
        }
    }
}
