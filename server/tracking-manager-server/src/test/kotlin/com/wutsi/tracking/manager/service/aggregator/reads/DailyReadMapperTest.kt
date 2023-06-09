package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.test.assertEquals

internal class DailyReadMapperTest {
    private val mapper = DailyReadMapper()

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyReadFilter.PAGE,
        event = DailyReadFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun map() {
        val result = mapper.map(track)
        assertEquals(track.productId, result.key.productId)
        assertEquals(1L, result.value)
    }
}
