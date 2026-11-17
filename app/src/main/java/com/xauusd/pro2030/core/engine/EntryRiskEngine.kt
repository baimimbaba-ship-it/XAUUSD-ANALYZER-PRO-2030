package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Direction

class EntryRiskEngine {
    data class Plan(val entry: Double, val stop: Double, val target: Double, val riskReward: Double, val riskPercentOfEquity: Double?)

    /** Validates user-supplied levels; does not recommend entry, stop, or target prices. */
    fun estimateRisk(
        direction: Direction,
        entry: Double,
        stop: Double,
        target: Double,
        accountEquity: Double? = null,
        quantity: Double? = null
    ): Plan? {
        if (direction == Direction.NEUTRAL || listOf(entry, stop, target).any { !it.isFinite() || it <= 0.0 }) return null
        val riskDistance: Double
        val rewardDistance: Double
        if (direction == Direction.BULLISH) {
            riskDistance = entry - stop
            rewardDistance = target - entry
        } else {
            riskDistance = stop - entry
            rewardDistance = entry - target
        }
        if (riskDistance <= 0.0 || rewardDistance <= 0.0) return null
        val riskPercent = if (accountEquity != null && quantity != null && accountEquity.isFinite() && quantity.isFinite() && accountEquity > 0.0 && quantity > 0.0) {
            riskDistance * quantity / accountEquity * 100.0
        } else null
        return Plan(entry, stop, target, rewardDistance / riskDistance, riskPercent)
    }
}
