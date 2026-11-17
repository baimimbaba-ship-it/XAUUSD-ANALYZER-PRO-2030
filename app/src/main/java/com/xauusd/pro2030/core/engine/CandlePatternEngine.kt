package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle

class CandlePatternEngine {
    enum class Pattern { DOJI, BULLISH_ENGULFING, BEARISH_ENGULFING }
    data class Match(val pattern: Pattern, val candleOpenTimeMillis: Long)

    fun detectPattern(candles: List<Candle>): List<Match> {
        val bars = candles.filter(Candle::isValid)
        return buildList {
            bars.forEachIndexed { index, current ->
                val range = current.high - current.low
                if (range > 0.0 && kotlin.math.abs(current.close - current.open) / range <= 0.1) {
                    add(Match(Pattern.DOJI, current.openTimeMillis))
                }
                if (index == 0) return@forEachIndexed
                val previous = bars[index - 1]
                val previousBearish = previous.close < previous.open
                val previousBullish = previous.close > previous.open
                val currentBullish = current.close > current.open
                val currentBearish = current.close < current.open
                if (previousBearish && currentBullish && current.open <= previous.close && current.close >= previous.open) {
                    add(Match(Pattern.BULLISH_ENGULFING, current.openTimeMillis))
                } else if (previousBullish && currentBearish && current.open >= previous.close && current.close <= previous.open) {
                    add(Match(Pattern.BEARISH_ENGULFING, current.openTimeMillis))
                }
            }
        }
    }
}
