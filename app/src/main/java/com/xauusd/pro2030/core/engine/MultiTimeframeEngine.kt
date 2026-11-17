package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import com.xauusd.pro2030.model.Timeframe

/** Aggregates only complete, valid lower-timeframe candles aligned to the requested bucket. */
class MultiTimeframeEngine {
    fun computeTimeframes(candles: List<Candle>, timeframe: Timeframe): List<Candle> {
        val valid = candles.filter(Candle::isValid).sortedBy { it.openTimeMillis }
        if (valid.isEmpty()) return emptyList()
        val groups = valid.groupBy { it.openTimeMillis / timeframe.durationMillis * timeframe.durationMillis }
        return groups.toSortedMap().mapNotNull { (start, group) ->
            val sorted = group.sortedBy { it.openTimeMillis }
            if (sorted.first().openTimeMillis != start || sorted.last().closeTimeMillis > start + timeframe.durationMillis) return@mapNotNull null
            Candle(
                openTimeMillis = start,
                closeTimeMillis = start + timeframe.durationMillis,
                open = sorted.first().open,
                high = sorted.maxOf { it.high },
                low = sorted.minOf { it.low },
                close = sorted.last().close,
                tickCount = sorted.sumOf { it.tickCount }
            )
        }
    }
}
