package com.wutsi.tracking.manager.service.aggregator.email

import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId

class DailyEmailMapperTest {
    private val mapper = DailyEmailMapper()

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyReadFilter.PAGE,
        event = DailyReadFilter.EVENT,
        productId = "123",
        accountId = "11",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun map() {
        val result = mapper.map(track)[0]
        assertEquals(EmailKey(track.accountId!!, track.productId!!), result.key)
        assertEquals(1L, result.value)
    }
}
