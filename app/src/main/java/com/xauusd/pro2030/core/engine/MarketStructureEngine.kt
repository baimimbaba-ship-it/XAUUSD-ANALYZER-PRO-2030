package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import com.xauusd.pro2030.model.Direction

class MarketStructureEngine {
    data class Structure(val direction: Direction, val swingHighs: List<Double>, val swingLows: List<Double>)

    /** A simple local-pivot description; it is not a prediction or a complete market-structure model. */
    fun analyzeTrendStructure(candles: List<Candle>, pivotWidth: Int = 2): Structure {
        require(pivotWidth > 0) { "pivotWidth must be positive" }
        val bars = candles.filter(Candle::isValid)
        if (bars.size < pivotWidth * 2 + 1) return Structure(Direction.NEUTRAL, emptyList(), emptyList())
        val highs = mutableListOf<Double>()
        val lows = mutableListOf<Double>()
        for (index in pivotWidth until bars.size - pivotWidth) {
            val window = bars.subList(index - pivotWidth, index + pivotWidth + 1)
            if (window.all { bars[index].high >= it.high }) highs += bars[index].high
            if (window.all { bars[index].low <= it.low }) lows += bars[index].low
        }
        val higherHighs = highs.size >= 2 && highs.last() > highs[highs.lastIndex - 1]
        val higherLows = lows.size >= 2 && lows.last() > lows[lows.lastIndex - 1]
        val lowerHighs = highs.size >= 2 && highs.last() < highs[highs.lastIndex - 1]
        val lowerLows = lows.size >= 2 && lows.last() < lows[lows.lastIndex - 1]
        val direction = when {
            higherHighs && higherLows -> Direction.BULLISH
            lowerHighs && lowerLows -> Direction.BEARISH
            else -> Direction.NEUTRAL
        }
        return Structure(direction, highs, lows)
    }
}
