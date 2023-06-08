package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.test.assertEquals

internal class ViewMapperTest {
    private val mapper = ViewMapper()

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = ViewMapper.PAGE,
        event = ViewMapper.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun map() {
        val result = mapper.map(track)
        assertEquals(track.productId, result?.key?.productId)
        assertEquals(1L, result?.value)
        assertEquals(track.businessId, result?.key?.businessId)
    }
}
