package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import com.xauusd.pro2030.model.Timeframe

/** Builds candles from actual quote observations; it does not invent volume or missing prices. */
class CandleEngine {
    data class QuoteObservation(val timestampMillis: Long, val price: Double)

    fun buildCandles(observations: List<QuoteObservation>, timeframe: Timeframe): List<Candle> {
        val valid = observations.filter { it.timestampMillis >= 0L && it.price.isFinite() && it.price > 0.0 }
            .sortedBy { it.timestampMillis }
        return valid.groupBy { it.timestampMillis / timeframe.durationMillis * timeframe.durationMillis }
            .toSortedMap()
            .map { (bucketStart, quotes) ->
                Candle(
                    openTimeMillis = bucketStart,
                    closeTimeMillis = bucketStart + timeframe.durationMillis,
                    open = quotes.first().price,
                    high = quotes.maxOf { it.price },
                    low = quotes.minOf { it.price },
                    close = quotes.last().price,
                    tickCount = quotes.size
                )
            }
    }

    fun updateCurrentCandle(candles: List<Candle>, observation: QuoteObservation, timeframe: Timeframe): List<Candle> {
        if (observation.timestampMillis < 0L || !observation.price.isFinite() || observation.price <= 0.0) return candles
        val start = observation.timestampMillis / timeframe.durationMillis * timeframe.durationMillis
        val end = start + timeframe.durationMillis
        val previous = candles.lastOrNull()
        if (previous == null || previous.openTimeMillis < start) {
            return candles + Candle(start, end, observation.price, observation.price, observation.price, observation.price, 1)
        }
        if (previous.openTimeMillis != start || !previous.isValid) return candles
        val updated = previous.copy(
            high = maxOf(previous.high, observation.price),
            low = minOf(previous.low, observation.price),
            close = observation.price,
            tickCount = previous.tickCount + 1
        )
        return candles.dropLast(1) + updated
    }
}
