package com.xauusd.pro2030.core.engine

import android.util.Log
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * BiQuote REST snapshot client. The official SDK documents GET /api/{symbol}
 * and the allowStale=false query parameter. No SignalR contract is used here.
 */
class BiQuoteConnectionEngine(
    private val endpoint: String = "https://biquote.io/api/XAUUSD?allowStale=false",
    private val pollIntervalSeconds: Long = 15L,
    private val staleAfterSeconds: Long = 60L
) {
    enum class Status { LIVE, STALE, RECONNECTING, DISCONNECTED, ERROR }

    data class Tick(
        val symbol: String,
        val mid: Double,
        val bid: Double?,
        val ask: Double?,
        val marketState: String,
        val quoteAgeSeconds: Long,
        val sourceTimestampMillis: Long?,
        val receivedTimestampMillis: Long
    ) {
        val spread: Double? get() = if (bid != null && ask != null) ask - bid else null
    }

    data class State(
        val status: Status,
        val tick: Tick? = null,
        val detail: String? = null,
        val source: String = "BiQuote REST"
    )

    fun interface Listener {
        fun onState(state: State)
    }

    private val lock = Any()
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val requestInFlight = AtomicBoolean(false)
    @Volatile private var listener: Listener? = null
    @Volatile private var running = false
    private var scheduledPoll: ScheduledFuture<*>? = null
    private var retryAttempt = 0

    /** Starts one polling loop. Repeated calls update the listener, not the connection. */
    fun start(stateListener: Listener) {
        synchronized(lock) {
            listener = stateListener
            if (running) return
            running = true
            retryAttempt = 0
            Log.i(TAG, "Starting BiQuote polling: $endpoint")
            publish(State(Status.DISCONNECTED, detail = "Starting connection"))
            executor.execute { fetchQuote() }
        }
    }

    /** Forces a real HTTP snapshot request using the documented REST endpoint. */
    fun refresh() {
        synchronized(lock) {
            if (!running) {
                Log.i(TAG, "Refresh requested while stopped; starting polling")
                listener?.let { start(it) }
                return
            }
            scheduledPoll?.cancel(false)
            Log.i(TAG, "Manual refresh requested")
            executor.execute { fetchQuote() }
        }
    }

    fun stop() {
        synchronized(lock) {
            if (!running) return
            running = false
            scheduledPoll?.cancel(false)
            scheduledPoll = null
            Log.i(TAG, "BiQuote polling stopped")
            publish(State(Status.DISCONNECTED, detail = "Monitoring stopped"))
        }
    }

    private fun fetchQuote() {
        if (!running || !requestInFlight.compareAndSet(false, true)) return
        try {
            val tick = requestAndParse()
            retryAttempt = 0
            val fresh = tick.marketState.equals("open", ignoreCase = true) &&
                tick.quoteAgeSeconds <= staleAfterSeconds
            val status = if (fresh) Status.LIVE else Status.STALE
            val detail = if (fresh) "Quote fresh" else {
                "Quote not current (market=${tick.marketState}, age=${tick.quoteAgeSeconds}s)"
            }
            Log.i(TAG, "BiQuote response received: status=$status age=${tick.quoteAgeSeconds}s")
            publish(State(status, tick, detail))
            scheduleNext(pollIntervalSeconds)
        } catch (error: IOException) {
            scheduleRetry(Status.RECONNECTING, error)
        } catch (error: Exception) {
            scheduleRetry(Status.ERROR, error)
        } finally {
            requestInFlight.set(false)
        }
    }

    private fun requestAndParse(): Tick {
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("Accept", "application/json")
        }
        try {
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                val body = connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                throw IOException("BiQuote HTTP $responseCode: ${body.take(200)}")
            }
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)
            val symbol = json.optString("symbol", "").uppercase()
            if (symbol != "XAUUSD") throw IllegalArgumentException("Unexpected or missing symbol in response")

            val mid = json.optDouble("mid", Double.NaN)
            if (!mid.isFinite() || mid <= 0.0) throw IllegalArgumentException("Missing or invalid mid price")
            val marketState = json.optString("marketState", "unknown")
            if (!json.has("quoteAgeSeconds") || json.isNull("quoteAgeSeconds")) {
                throw IllegalArgumentException("Missing quoteAgeSeconds; cannot establish quote freshness")
            }
            val age = json.optLong("quoteAgeSeconds", -1L)
            if (age < 0L) throw IllegalArgumentException("Invalid quoteAgeSeconds")
            if (!json.has("stale") || json.isNull("stale")) {
                throw IllegalArgumentException("Missing stale flag; cannot establish quote freshness")
            }
            val explicitlyStale = json.optBoolean("stale", true)

            val bid = optionalPositiveNumber(json, "bid")
            val ask = optionalPositiveNumber(json, "ask")
            val receivedAt = System.currentTimeMillis()

            return Tick(
                symbol = symbol,
                mid = mid,
                bid = bid,
                ask = ask,
                marketState = if (explicitlyStale) "stale" else marketState,
                quoteAgeSeconds = age,
                // BiQuote SDK documents marketState/stale/quoteAgeSeconds but does not
                // document a stable source timestamp field in the verified contract.
                sourceTimestampMillis = null,
                receivedTimestampMillis = receivedAt
            )
        } finally {
            connection.disconnect()
        }
    }

    private fun optionalPositiveNumber(json: JSONObject, field: String): Double? {
        if (!json.has(field) || json.isNull(field)) return null
        val value = json.optDouble(field, Double.NaN)
        return value.takeIf { it.isFinite() && it > 0.0 }
    }

    private fun scheduleRetry(status: Status, error: Exception) {
        retryAttempt += 1
        val exponent = (retryAttempt - 1).coerceAtMost(6)
        val delay = (2L shl exponent).coerceAtMost(MAX_RETRY_SECONDS)
        Log.w(TAG, "BiQuote request failed; attempt=$retryAttempt retryIn=${delay}s", error)
        publish(State(status, detail = error.message ?: error.javaClass.simpleName))
        scheduleNext(delay)
    }

    private fun scheduleNext(delaySeconds: Long) {
        synchronized(lock) {
            if (!running) return
            scheduledPoll?.cancel(false)
            scheduledPoll = executor.schedule({ fetchQuote() }, delaySeconds, TimeUnit.SECONDS)
        }
    }

    private fun publish(state: State) {
        listener?.onState(state)
    }

    companion object {
        private const val TAG = "BiQuoteConnection"
        private const val MAX_RETRY_SECONDS = 60L
    }
}
