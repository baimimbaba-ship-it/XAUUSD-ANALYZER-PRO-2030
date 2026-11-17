package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IndicatorEngineTest {
    private fun candle(index: Int, close: Double) = Candle(index * 60_000L, (index + 1) * 60_000L, close, close, close, close, 1)

    @Test fun calculatesSmaAndRsiFromKnownCloses() {
        val values = IndicatorEngine().calculateIndicators(listOf(candle(0, 1.0), candle(1, 2.0), candle(2, 3.0)), period = 2)
        assertEquals(2.5, values.sma!!, 1e-9)
        assertEquals(100.0, values.rsi!!, 1e-9)
        assertEquals(2.5, values.ema!!, 1e-9)
    }

    @Test fun returnsNullForInsufficientHistory() {
        val values = IndicatorEngine().calculateIndicators(listOf(candle(0, 1.0)), period = 2)
        assertNull(values.sma)
        assertNull(values.ema)
        assertNull(values.rsi)
        assertNull(values.atr)
    }
}
