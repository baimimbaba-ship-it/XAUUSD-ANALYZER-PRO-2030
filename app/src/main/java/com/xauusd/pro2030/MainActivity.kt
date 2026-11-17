package com.xauusd.pro2030

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xauusd.pro2030.core.engine.BiQuoteConnectionEngine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val biQuote = BiQuoteConnectionEngine()
    private lateinit var priceText: TextView
    private lateinit var bidText: TextView
    private lateinit var askText: TextView
    private lateinit var spreadText: TextView
    private lateinit var timestampText: TextView
    private lateinit var statusText: TextView
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        priceText = findViewById(R.id.priceText)
        bidText = findViewById(R.id.bidText)
        askText = findViewById(R.id.askText)
        spreadText = findViewById(R.id.spreadText)
        timestampText = findViewById(R.id.timestampText)
        statusText = findViewById(R.id.statusText)
        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener { biQuote.refresh() }
        render(BiQuoteConnectionEngine.State(BiQuoteConnectionEngine.Status.DISCONNECTED, detail = "Waiting to connect"))
    }

    override fun onResume() {
        super.onResume()
        biQuote.start { state -> runOnUiThread { render(state) } }
    }

    override fun onPause() {
        biQuote.stop()
        super.onPause()
    }

    private fun render(state: BiQuoteConnectionEngine.State) {
        statusText.text = buildString {
            append(state.status.name)
            state.detail?.let { append(" · "); append(it) }
        }
        refreshButton.isEnabled = state.status != BiQuoteConnectionEngine.Status.RECONNECTING
        val tick = state.tick
        if (tick == null) {
            priceText.text = "—"
            bidText.text = "Bid: —"
            askText.text = "Ask: —"
            spreadText.text = "Spread: —"
            timestampText.text = "No current quote · Source: ${state.source}"
            return
        }

        val staleLabel = if (state.status == BiQuoteConnectionEngine.Status.STALE) "LAST KNOWN · STALE" else "LIVE"
        priceText.text = "${String.format(Locale.US, "%.2f", tick.mid)}  ·  $staleLabel"
        bidText.text = tick.bid?.let { "Bid: ${String.format(Locale.US, "%.2f", it)}" } ?: "Bid: unavailable"
        askText.text = tick.ask?.let { "Ask: ${String.format(Locale.US, "%.2f", it)}" } ?: "Ask: unavailable"
        spreadText.text = tick.spread?.let { "Spread: ${String.format(Locale.US, "%.2f", it)}" } ?: "Spread: unavailable"
        val received = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(Date(tick.receivedTimestampMillis))
        timestampText.text = "Received: $received · Source time: unavailable · ${state.source}"
    }
}
