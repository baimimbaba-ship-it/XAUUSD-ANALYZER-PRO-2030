package com.xauusd.pro2030.core.engine

/** Evaluates explicit user-configured price conditions; dispatch is handled by the app layer. */
class AlertEngine {
    enum class Crossing { ABOVE, BELOW }
    data class PriceAlert(val threshold: Double, val crossing: Crossing)

    fun triggerAlert(price: Double, alert: PriceAlert): Boolean {
        require(alert.threshold.isFinite() && alert.threshold > 0.0) { "threshold must be positive and finite" }
        return price.isFinite() && when (alert.crossing) {
            Crossing.ABOVE -> price >= alert.threshold
            Crossing.BELOW -> price <= alert.threshold
        }
    }
}
