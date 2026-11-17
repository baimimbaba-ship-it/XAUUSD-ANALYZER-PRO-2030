package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle

class SupplyDemandEngine {
    enum class Type { SUPPLY, DEMAND }
    data class Zone(val type: Type, val low: Double, val high: Double, val sourceCandleOpenTimeMillis: Long)

    /** Exposes local pivot candles as candidate zones; callers must label them heuristic. */
    fun detectZones(candles: List<Candle>, pivotWidth: Int = 2): List<Zone> {
        require(pivotWidth > 0) { "pivotWidth must be positive" }
        val bars = candles.filter(Candle::isValid)
        if (bars.size < pivotWidth * 2 + 1) return emptyList()
        return (pivotWidth until bars.size - pivotWidth).mapNotNull { index ->
            val current = bars[index]
            val window = bars.subList(index - pivotWidth, index + pivotWidth + 1)
            when {
                window.all { current.high >= it.high } -> Zone(Type.SUPPLY, current.low, current.high, current.openTimeMillis)
                window.all { current.low <= it.low } -> Zone(Type.DEMAND, current.low, current.high, current.openTimeMillis)
                else -> null
            }
        }
    }
}
