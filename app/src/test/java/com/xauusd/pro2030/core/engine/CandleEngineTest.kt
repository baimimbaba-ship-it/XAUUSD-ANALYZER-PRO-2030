package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Timeframe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CandleEngineTest {
    @Test fun buildsOhlcFromObservedQuotesAndSkipsInvalidValues() {
        val engine = CandleEngine()
        val candles = engine.buildCandles(
            listOf(
                CandleEngine.QuoteObservation(1_000L, 10.0),
                CandleEngine.QuoteObservation(2_000L, 12.0),
                CandleEngine.QuoteObservation(3_000L, Double.NaN),
                CandleEngine.QuoteObservation(61_000L, 11.0)
            ),
            Timeframe.M1
        )
        assertEquals(2, candles.size)
        assertEquals(10.0, candles[0].open, 0.0)
        assertEquals(12.0, candles[0].high, 0.0)
        assertEquals(10.0, candles[0].low, 0.0)
        assertEquals(12.0, candles[0].close, 0.0)
        assertTrue(candles.all { it.isValid })
    }
}
