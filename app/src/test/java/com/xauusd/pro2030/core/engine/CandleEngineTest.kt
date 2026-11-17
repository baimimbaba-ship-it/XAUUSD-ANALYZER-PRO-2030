package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CandleEngineTest {
    @Test
    fun aggregatesQuotesIntoOhlcAndClosesWhenNextBucketArrives() {
        val engine = CandleEngine(60_000)
        assertNull(engine.addPrice(10.0, 1_000))
        engine.addPrice(12.0, 20_000)
        engine.addPrice(9.0, 40_000)
        val closed = engine.addPrice(11.0, 60_000)!!
        assertEquals(Candle(0, 60_000, 10.0, 12.0, 9.0, 9.0, 3), closed)
        assertEquals(11.0, engine.candles(includeCurrent = true).last().close, 0.0)
    }

    @Test
    fun rejectsInvalidAndOutOfOrderQuotes() {
        val engine = CandleEngine()
        assertThrows(IllegalArgumentException::class.java) { engine.addPrice(0.0, 1_000) }
        engine.addPrice(10.0, 60_000)
        assertThrows(IllegalArgumentException::class.java) { engine.addPrice(11.0, 59_000) }
    }
}
