package com.xauusd.pro2030.core.engine

class SignalScoringEngine {
    fun scoreSignal(changePercent: Double): Int {
        return when {
            changePercent >= 0.5 -> 82
            changePercent >= 0.2 -> 70
            changePercent >= 0.0 -> 61
            changePercent <= -0.5 -> 76
            changePercent <= -0.2 -> 66
            else -> 58
        }
    }
}
