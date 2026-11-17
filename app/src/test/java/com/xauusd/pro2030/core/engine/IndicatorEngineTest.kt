package com.xauusd.pro2030.core.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IndicatorEngineTest {
    private val engine = IndicatorEngine()

    @Test
    fun doesNotInventValuesWithoutEnoughPrices() {
        assertNull(engine.calculate((1..14).map(Int::toDouble)))
    }

    @Test
    fun calculatesMovingAveragesAndRsiForRisingSeries() {
        val result = engine.calculate((1..15).map(Int::toDouble))!!
        assertEquals(8.5, result.sma, 0.000001)
        assertEquals(15.0, result.ema, 0.000001)
        assertEquals(100.0, result.rsi, 0.000001)
    }

    @Test
    fun flatSeriesHasNeutralRsi() {
        val result = engine.calculate(List(15) { 2000.0 })!!
        assertEquals(50.0, result.rsi, 0.000001)
    }
}
