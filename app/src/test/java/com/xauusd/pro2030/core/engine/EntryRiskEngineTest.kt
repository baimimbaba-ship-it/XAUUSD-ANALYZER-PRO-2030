package com.xauusd.pro2030.core.engine

import com.xauusd.pro2030.model.Direction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EntryRiskEngineTest {
    @Test fun computesRiskRewardForUserProvidedBullishLevels() {
        val plan = EntryRiskEngine().estimateRisk(Direction.BULLISH, entry = 100.0, stop = 98.0, target = 104.0)
        assertEquals(2.0, plan!!.riskReward, 1e-9)
    }

    @Test fun rejectsLevelsOnWrongSideOfEntry() {
        assertNull(EntryRiskEngine().estimateRisk(Direction.BULLISH, entry = 100.0, stop = 101.0, target = 104.0))
    }
}
