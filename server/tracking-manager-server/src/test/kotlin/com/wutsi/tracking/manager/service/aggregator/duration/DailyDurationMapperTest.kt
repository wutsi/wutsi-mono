package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId

class DailyDurationMapperTest {
    private val mapper = DailyDurationMapper()

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyReadFilter.PAGE,
        event = DailyReadFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun map() {
        val result = mapper.map(track)[0]
        assertEquals(track.productId, result.key.productId)
        assertEquals(track.correlationId, result.key.correlationId)
        assertEquals(track.time, result.value)
    }
}
