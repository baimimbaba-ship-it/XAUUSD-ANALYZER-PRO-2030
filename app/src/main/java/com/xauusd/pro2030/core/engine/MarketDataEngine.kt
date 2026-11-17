package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.MarketSnapshot
import com.xauusd.pro2030.model.SignalRecommendation

class MarketDataEngine {
    fun getDemoSnapshot(): MarketSnapshot {
        val currentPrice = 2348.75
        val changePercent = 0.68
        val volume = 104200L

        return MarketSnapshot(
            symbol = "XAUUSD",
            lastPrice = currentPrice,
            bid = currentPrice - 0.18,
            ask = currentPrice + 0.18,
            changePercent = changePercent,
            trendBias = if (changePercent >= 0.0) "BULLISH" else "BEARISH",
            volume = volume
        )
    }

    fun getSignalRecommendation(snapshot: MarketSnapshot): SignalRecommendation {
        val score = when {
            snapshot.changePercent >= 0.5 -> 82
            snapshot.changePercent >= 0.2 -> 70
            snapshot.changePercent >= 0.0 -> 61
            snapshot.changePercent <= -0.5 -> 76
            snapshot.changePercent <= -0.2 -> 66
            else -> 58
        }

        val direction = when {
            snapshot.changePercent >= 0.2 -> "BUY"
            snapshot.changePercent <= -0.2 -> "SELL"
            else -> "WAIT"
        }

        val reason = when {
            snapshot.changePercent >= 0.5 -> "Momentum bullish kuat dan harga berpegang di atas support utama."
            snapshot.changePercent <= -0.5 -> "Tekanan jual kuat dengan struktur turun dominan."
            else -> "Pasar condong netral, tunggu konfirmasi bentuk candle berikutnya."
        }

        return SignalRecommendation(direction = direction, score = score, reason = reason)
    }
}
