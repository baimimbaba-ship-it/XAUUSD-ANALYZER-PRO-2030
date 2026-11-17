package com.xauusd.pro2030.model

data class MarketSnapshot(
    val symbol: String = "XAUUSD",
    val lastPrice: Double = 0.0,
    val bid: Double = 0.0,
    val ask: Double = 0.0,
    val changePercent: Double = 0.0,
    val trendBias: String = "NEUTRAL",
    val volume: Long = 0L
)
