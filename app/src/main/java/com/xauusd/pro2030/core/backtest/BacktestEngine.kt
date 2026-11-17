package com.xauusd.pro2030.core/backtest

import com.xauusd.pro2030.model.Candle

/** Historical simulation utility. The caller supplies strategy and explicit per-side costs. */
class BacktestEngine {
    enum class Action { LONG, SHORT, FLAT }
    data class Trade(val entryTimeMillis: Long, val exitTimeMillis: Long, val direction: Action, val entry: Double, val exit: Double, val netPoints: Double)
    data class Result(val trades: List<Trade>, val totalNetPoints: Double)

    fun runBacktest(
        candles: List<Candle>,
        costPerSidePoints: Double,
        strategy: (history: List<Candle>, current: Candle) -> Action
    ): Result {
        require(costPerSidePoints.isFinite() && costPerSidePoints >= 0.0) { "costPerSidePoints must be explicit and non-negative" }
        val bars = candles.filter(Candle::isValid).sortedBy { it.openTimeMillis }
        var position: Action? = null
        var entry: Candle? = null
        val trades = mutableListOf<Trade>()
        bars.forEachIndexed { index, candle ->
            val action = strategy(bars.take(index), candle)
            val desired = action.takeIf { it != Action.FLAT }
            if (position != desired && position != null && entry != null) {
                val sign = if (position == Action.LONG) 1.0 else -1.0
                val points = (candle.close - entry!!.close) * sign - costPerSidePoints * 2.0
                trades += Trade(entry!!.openTimeMillis, candle.closeTimeMillis, position!!, entry!!.close, candle.close, points)
                entry = null
            }
            if (position != desired) {
                position = desired
                if (desired != null) entry = candle
            }
        }
        return Result(trades, trades.sumOf { it.netPoints })
    }
}
