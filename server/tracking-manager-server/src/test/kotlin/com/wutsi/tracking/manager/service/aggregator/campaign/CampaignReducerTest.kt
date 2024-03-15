package com.wutsi.tracking.manager.service.aggregator.campaign

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CampaignReducerTest {
    private val reducer = CampaignReducer()

    @Test
    fun reduce() {
        val acc = CampaignValue(CampaignKey("campaign-1"), 10)
        val cur = CampaignValue(CampaignKey("campaign-1"), 1)
        val result = reducer.reduce(listOf(acc, cur))

        assertEquals("campaign-1", result.key.campaign)
        assertEquals(acc.value + cur.value, result.value)
    }
}