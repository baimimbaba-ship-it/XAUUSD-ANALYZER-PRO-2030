package com.xauusd.pro2030.data

data class SignalHistory(
    val id: Long = 0L,
    val symbol: String = "XAUUSD",
    val signalType: String = "WAIT",
    val score: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
