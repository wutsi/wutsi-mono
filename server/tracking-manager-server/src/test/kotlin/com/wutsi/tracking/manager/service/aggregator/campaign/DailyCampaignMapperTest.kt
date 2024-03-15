package com.wutsi.tracking.manager.service.aggregator.campaign

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId

class DailyCampaignMapperTest {
    private val mapper = DailyCampaignMapper()

    private val track = Fixtures.createTrackEntity(
        bot = false,
        campaign = "CMP-120932-10",
        event = DailyCampaignFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun map() {
        val result = mapper.map(track)[0]
        Assertions.assertEquals(track.campaign, result.key.campaign)
        Assertions.assertEquals(1L, result.value)
    }
}