package com.xauusd.pro2030.core.backtest

import com.xauusd.pro2030.model.Candle
import org.junit.Assert.assertEquals
import org.junit.Test

class BacktestEngineTest {
    @Test fun closesPositionWhenStrategyTurnsFlatAndAppliesRoundTripCost() {
        val bars = listOf(
            Candle(0, 60_000, 100.0, 101.0, 99.0, 100.0, 1),
            Candle(60_000, 120_000, 100.0, 103.0, 100.0, 102.0, 1),
            Candle(120_000, 180_000, 102.0, 103.0, 101.0, 103.0, 1)
        )
        val result = BacktestEngine().runBacktest(bars, costPerSidePoints = 0.5) { _, current ->
            if (current.openTimeMillis < 120_000L) BacktestEngine.Action.LONG else BacktestEngine.Action.FLAT
        }
        assertEquals(1, result.trades.size)
        assertEquals(2.0, result.totalNetPoints, 1e-9)
    }
}
