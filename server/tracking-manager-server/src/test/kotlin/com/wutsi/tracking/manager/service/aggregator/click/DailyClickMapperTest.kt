package com.wutsi.tracking.manager.service.aggregator.click

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId

class DailyClickMapperTest {
    private val mapper = DailyClickMapper()

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyClickFilter.PAGE,
        event = DailyClickFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun map() {
        val result = mapper.map(track)[0]
        assertEquals(track.productId, result.key.productId)
        assertEquals(1L, result.value)
    }
}
