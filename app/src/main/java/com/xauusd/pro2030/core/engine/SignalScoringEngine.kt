package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Candle
import com.xauusd.pro2030.model.SignalRecommendation

/** Creates traceable educational summaries; the score is a rule-strength index, not a probability. */
class SignalScoringEngine(private val indicators: IndicatorEngine = IndicatorEngine()) {
    data class Analysis(
        val recommendation: SignalRecommendation,
        val timeframe: String,
        val source: String,
        val generatedAtMillis: Long,
        val lastClose: Double,
        val sma: Double,
        val ema: Double,
        val rsi: Double
    )

    fun analyze(
        candles: List<Candle>,
        timeframe: String,
        source: String,
        generatedAtMillis: Long = System.currentTimeMillis(),
        period: Int = IndicatorEngine.DEFAULT_PERIOD
    ): Analysis? {
        require(timeframe.isNotBlank()) { "Timeframe must be specified" }
        require(source.isNotBlank()) { "Data source must be specified" }
        val ordered = candles.sortedBy { it.openTimeMillis }
        val values = indicators.calculate(ordered.map { it.close }, period) ?: return null
        val lastClose = ordered.last().close
        val aboveAverage = lastClose > values.sma && lastClose > values.ema
        val belowAverage = lastClose < values.sma && lastClose < values.ema
        val rsiOversold = values.rsi <= 30.0
        val rsiOverbought = values.rsi >= 70.0
        val direction = when {
            aboveAverage && !rsiOverbought -> "BUY"
            belowAverage && !rsiOversold -> "SELL"
            else -> "WAIT"
        }
        val reasons = buildList {
            add(if (aboveAverage) "Close is above SMA and EMA" else if (belowAverage) "Close is below SMA and EMA" else "Close is between or equal to moving averages")
            add("RSI(${values.period}) = ${"%.2f".format(java.util.Locale.US, values.rsi)}")
            if (rsiOversold) add("RSI is at or below 30 (oversold rule)")
            if (rsiOverbought) add("RSI is at or above 70 (overbought rule)")
            add("Based on ${ordered.size} sampled candles from $source")
        }
        val aligned = (if (aboveAverage || belowAverage) 1 else 0) + (if ((direction == "BUY" && values.rsi < 70.0) || (direction == "SELL" && values.rsi > 30.0)) 1 else 0)
        val score = if (direction == "WAIT") 0 else aligned * 50
        return Analysis(
            SignalRecommendation(direction, score, reasons.joinToString("; ")),
            timeframe,
            source,
            generatedAtMillis,
            lastClose,
            values.sma,
            values.ema,
            values.rsi
        )
    }

    /** Retained for old callers; this heuristic must not be interpreted as a probability. */
    fun scoreSignal(changePercent: Double): Int {
        require(changePercent.isFinite()) { "Change must be finite" }
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
