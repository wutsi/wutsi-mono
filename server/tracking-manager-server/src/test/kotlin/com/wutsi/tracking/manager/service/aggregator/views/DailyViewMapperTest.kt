package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId

class DailyViewMapperTest {
    private val mapper = DailyViewMapper()

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyViewFilter.PAGE,
        event = DailyViewFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun map() {
        val result = mapper.map(track)[0]
        kotlin.test.assertEquals(track.productId, result.key.productId)
        kotlin.test.assertEquals(1L, result.value)
    }
}
