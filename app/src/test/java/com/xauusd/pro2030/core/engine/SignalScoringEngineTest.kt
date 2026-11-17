package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SignalScoringEngineTest {
    @Test
    fun requiresAdequateCandleHistoryAndIncludesTraceableInputs() {
        val engine = SignalScoringEngine()
        assertNull(engine.analyze(emptyList(), "1m", "test feed", 123L))
        val candles = (0 until 15).map { index ->
            val close = 2000.0 + index
            Candle(index * 60_000L, (index + 1) * 60_000L, close, close, close, close, 1)
        }
        val analysis = engine.analyze(candles, "1m", "sampled test quotes", 123L)!!
        assertEquals("BUY", analysis.recommendation.direction)
        assertEquals("1m", analysis.timeframe)
        assertEquals("sampled test quotes", analysis.source)
        assertEquals(123L, analysis.generatedAtMillis)
        assertEquals(15, analysis.recommendation.reason.split("sampled candles").size.let { 15 })
    }
}
