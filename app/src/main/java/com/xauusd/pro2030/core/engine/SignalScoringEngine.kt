package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import com.xauusd.pro2030.model.Direction
import com.xauusd.pro2030.model.IndicatorValues
import com.xauusd.pro2030.model.SignalScore

/** Transparent rule alignment only; scores are not probabilities or trading advice. */
class SignalScoringEngine {
    fun scoreSignal(candles: List<Candle>, indicators: IndicatorValues): SignalScore? {
        val last = candles.lastOrNull(Candle::isValid) ?: return null
        val reasons = mutableListOf<String>()
        var bullish = 0
        var bearish = 0
        indicators.ema?.let {
            when {
                last.close > it -> { bullish += 1; reasons += "Close above EMA" }
                last.close < it -> { bearish += 1; reasons += "Close below EMA" }
            }
        }
        indicators.rsi?.let {
            when {
                it >= 55.0 -> { bullish += 1; reasons += "RSI at or above 55" }
                it <= 45.0 -> { bearish += 1; reasons += "RSI at or below 45" }
                else -> reasons += "RSI neutral zone"
            }
        }
        when {
            last.close > last.open -> { bullish += 1; reasons += "Latest candle bullish" }
            last.close < last.open -> { bearish += 1; reasons += "Latest candle bearish" }
        }
        if (bullish + bearish == 0) return null
        val direction = when {
            bullish > bearish -> Direction.BULLISH
            bearish > bullish -> Direction.BEARISH
            else -> Direction.NEUTRAL
        }
        val aligned = maxOf(bullish, bearish)
        return SignalScore(score = aligned * 100 / (bullish + bearish), direction = direction, reasons = reasons)
    }
}
