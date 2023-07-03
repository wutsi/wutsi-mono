package com.wutsi.tracking.manager.service.aggregator.readers

import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.service.aggregator.reader.DailyReaderMapper
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.test.assertEquals

internal class DailyReaderMapperTest {
    private val mapper = DailyReaderMapper()

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyReadFilter.PAGE,
        event = DailyReadFilter.EVENT,
        productId = "123",
        accountId = "1",
        deviceId = "device-1",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun map() {
        val result = mapper.map(track)
        assertEquals(track.accountId, result.key.accountId)
        assertEquals(track.deviceId, result.key.deviceId)
        assertEquals(track.productId, result.key.productId)
        assertEquals(1L, result.value)
    }
}
