package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle

/** Builds explicitly sampled OHLC candles from received quote snapshots; it does not fetch historical candles. */
class CandleEngine(private val timeframeMillis: Long = DEFAULT_TIMEFRAME_MILLIS) {
    private data class Bucket(
        val openTime: Long,
        var open: Double,
        var high: Double,
        var low: Double,
        var close: Double,
        var samples: Int
    )

    private val completed = mutableListOf<Candle>()
    private var current: Bucket? = null

    init {
        require(timeframeMillis > 0L) { "Timeframe must be positive" }
    }

    @Synchronized
    fun addPrice(price: Double, timestampMillis: Long): Candle? {
        require(price.isFinite() && price > 0.0) { "Price must be finite and positive" }
        require(timestampMillis >= 0L) { "Timestamp must be non-negative" }
        val bucketStart = timestampMillis / timeframeMillis * timeframeMillis
        val active = current
        if (active == null) {
            current = Bucket(bucketStart, price, price, price, price, 1)
            return null
        }
        require(bucketStart >= active.openTime) { "Out-of-order quote received" }
        if (bucketStart == active.openTime) {
            active.high = maxOf(active.high, price)
            active.low = minOf(active.low, price)
            active.close = price
            active.samples += 1
            return null
        }

        val closed = active.toCandle(timeframeMillis)
        completed += closed
        current = Bucket(bucketStart, price, price, price, price, 1)
        return closed
    }

    @Synchronized
    fun candles(includeCurrent: Boolean = false): List<Candle> = buildList {
        addAll(completed)
        if (includeCurrent) current?.let { add(it.toCandle(timeframeMillis)) }
    }

    @Synchronized
    fun clear() {
        completed.clear()
        current = null
    }

    private fun Bucket.toCandle(duration: Long) = Candle(
        openTimeMillis = openTime,
        closeTimeMillis = openTime + duration,
        open = open,
        high = high,
        low = low,
        close = close,
        sampleCount = samples
    )

    companion object {
        const val DEFAULT_TIMEFRAME_MILLIS = 60_000L
    }
}
