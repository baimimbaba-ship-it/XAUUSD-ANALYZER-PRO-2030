package com.xauusd.pro2030.core.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class BiQuoteConnectionEngineTest {
    private val now = 1_700_000_000_000L

    @Test
    fun parsesValidQuoteAndOptionalPrices() {
        val tick = BiQuoteConnectionEngine.parseTick(
            """{"symbol":"XAUUSD","mid":2300.25,"bid":2300.0,"ask":2300.5,"marketState":"open","quoteAgeSeconds":10,"stale":false}""",
            now
        )
        assertEquals("XAUUSD", tick.symbol)
        assertEquals(2300.25, tick.mid, 0.0)
        assertEquals(0.5, tick.spread!!, 0.000001)
        assertEquals(now, tick.receivedTimestampMillis)
        assertNull(tick.sourceTimestampMillis)
    }

    @Test
    fun rejectsMissingFreshnessFieldsAndInvalidQuote() {
        assertThrows(IllegalArgumentException::class.java) {
            BiQuoteConnectionEngine.parseTick("""{"symbol":"XAUUSD","mid":2300,"stale":false}""")
        }
        assertThrows(IllegalArgumentException::class.java) {
            BiQuoteConnectionEngine.parseTick("""{"symbol":"EURUSD","mid":1.2,"marketState":"open","quoteAgeSeconds":1,"stale":false}""")
        }
        assertThrows(IllegalArgumentException::class.java) {
            BiQuoteConnectionEngine.parseTick("""{"symbol":"XAUUSD","mid":0,"marketState":"open","quoteAgeSeconds":1,"stale":false}""")
        }
        assertThrows(IllegalArgumentException::class.java) {
            BiQuoteConnectionEngine.parseTick("""{"symbol":"XAUUSD","mid":2300,"marketState":"open","quoteAgeSeconds":-1,"stale":false}""")
        }
    }

    @Test
    fun explicitStaleFlagOverridesOpenMarketState() {
        val tick = BiQuoteConnectionEngine.parseTick(
            """{"symbol":"XAUUSD","mid":2300,"marketState":"open","quoteAgeSeconds":1,"stale":true}""",
            now
        )
        assertEquals(BiQuoteConnectionEngine.Status.STALE, BiQuoteConnectionEngine.classifyStatus(tick, 60))
    }

    @Test
    fun liveRequiresOpenMarketAndFreshQuoteAtBoundary() {
        val fresh = tick(marketState = "OPEN", age = 60)
        assertEquals(BiQuoteConnectionEngine.Status.LIVE, BiQuoteConnectionEngine.classifyStatus(fresh, 60))
        assertEquals(BiQuoteConnectionEngine.Status.STALE, BiQuoteConnectionEngine.classifyStatus(tick(age = 61), 60))
        assertEquals(BiQuoteConnectionEngine.Status.STALE, BiQuoteConnectionEngine.classifyStatus(tick(marketState = "closed"), 60))
    }

    @Test
    fun freshStatusHasClearDetail() {
        assertEquals("Quote fresh", BiQuoteConnectionEngine.statusDetail(tick(), 60))
        assertNotNull(BiQuoteConnectionEngine.statusDetail(tick(age = 61), 60))
    }

    private fun tick(
        marketState: String = "open",
        age: Long = 1
    ) = BiQuoteConnectionEngine.Tick("XAUUSD", 2300.0, null, null, marketState, age, null, now)
}
