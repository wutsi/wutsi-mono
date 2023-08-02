package com.wutsi.tracking.manager.service.aggregator.from

import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId

internal class DailyFromMapperTest {
    private val mapper = DailyFromMapper()

    @Test
    fun map() {
        val track = Fixtures.createTrackEntity(
            bot = false,
            page = DailyReadFilter.PAGE,
            event = DailyReadFilter.EVENT,
            productId = "123",
            time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
            url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email&utm_from=read-also",
        )
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals("read-also", result.key.from)
        assertEquals(1L, result.value)
    }

    @Test
    fun map2() {
        val track = Fixtures.createTrackEntity(
            bot = false,
            page = DailyReadFilter.PAGE,
            event = DailyReadFilter.EVENT,
            productId = "123",
            time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
            url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_from=read-also&utm_medium=email",
        )
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals("read-also", result.key.from)
        assertEquals(1L, result.value)
    }

    @Test
    fun mapEmpty() {
        val track = Fixtures.createTrackEntity(
            bot = false,
            page = DailyReadFilter.PAGE,
            event = DailyReadFilter.EVENT,
            productId = "123",
            time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
            url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
        )
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals("DIRECT", result.key.from)
        assertEquals(1L, result.value)
    }
}
