package com.xauusd.pro2030

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xauusd.pro2030.core.engine.MarketDataEngine

class MainActivity : AppCompatActivity() {
    private val marketDataEngine = MarketDataEngine()

    private lateinit var priceText: TextView
    private lateinit var changeText: TextView
    private lateinit var trendText: TextView
    private lateinit var volumeText: TextView
    private lateinit var signalText: TextView
    private lateinit var scoreText: TextView
    private lateinit var reasonText: TextView
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        priceText = findViewById(R.id.priceText)
        changeText = findViewById(R.id.changeText)
        trendText = findViewById(R.id.trendText)
        volumeText = findViewById(R.id.volumeText)
        signalText = findViewById(R.id.signalText)
        scoreText = findViewById(R.id.scoreText)
        reasonText = findViewById(R.id.reasonText)
        refreshButton = findViewById(R.id.refreshButton)

        refreshButton.setOnClickListener { renderDashboard() }
        renderDashboard()
    }

    private fun renderDashboard() {
        val snapshot = marketDataEngine.getDemoSnapshot()
        val signal = marketDataEngine.getSignalRecommendation(snapshot)

        priceText.text = String.format("%.2f", snapshot.lastPrice)
        changeText.text = String.format("%.2f%%", snapshot.changePercent)
        trendText.text = snapshot.trendBias
        volumeText.text = "${snapshot.volume}"

        signalText.text = signal.direction
        scoreText.text = "${signal.score}/100"
        reasonText.text = signal.reason
    }
}
