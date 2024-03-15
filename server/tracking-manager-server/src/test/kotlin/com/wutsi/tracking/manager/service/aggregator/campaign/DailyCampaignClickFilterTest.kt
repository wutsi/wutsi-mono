package com.wutsi.tracking.manager.service.aggregator.campaign

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

class DailyCampaignClickFilterTest {
    private val filter = DailyCampaignClickFilter(LocalDate.now(ZoneId.of("UTC")))

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = "xxx",
        event = DailyCampaignClickFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
        campaign = "1111"
    )

    @Test
    fun accept() {
        assertTrue(filter.accept(track))
    }

    @Test
    fun rejectBot() {
        assertFalse(filter.accept(track.copy(bot = true)))
    }

    @Test
    fun rejectCampaignNull() {
        assertFalse(filter.accept(track.copy(campaign = null)))
    }

    @Test
    fun rejectCampaignEmpty() {
        assertFalse(filter.accept(track.copy(campaign = "")))
    }

    @Test
    fun rejectBadEvent() {
        assertFalse(filter.accept(track.copy(event = "xxx")))
    }

    @Test
    fun rejectBadTime() {
        assertFalse(
            filter.accept(
                track.copy(time = OffsetDateTime.now().minusDays(1).toInstant().toEpochMilli()),
            ),
        )
    }
}