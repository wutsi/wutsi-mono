package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.test.assertEquals

internal class ReadMapperTest {
    private val mapper = ReadMapper()

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = ReadFilter.PAGE,
        event = ReadFilter.EVENT,
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
