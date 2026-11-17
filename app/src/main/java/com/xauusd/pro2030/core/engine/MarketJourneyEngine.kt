package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import com.xauusd.pro2030.model.Direction

class MarketJourneyEngine {
    data class Summary(val direction: Direction, val latestClose: Double?, val candlesObserved: Int, val asOfMillis: Long?)

    /** Summarizes observed candle direction only; it does not forecast future movement. */
    fun summarizeMarketState(candles: List<Candle>): Summary {
        val bars = candles.filter(Candle::isValid)
        val direction = when {
            bars.size < 2 -> Direction.NEUTRAL
            bars.last().close > bars.first().close -> Direction.BULLISH
            bars.last().close < bars.first().close -> Direction.BEARISH
            else -> Direction.NEUTRAL
        }
        return Summary(direction, bars.lastOrNull()?.close, bars.size, bars.lastOrNull()?.closeTimeMillis)
    }
}
