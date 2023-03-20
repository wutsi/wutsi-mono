package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ProductViewMapperTest {
    private val mapper = ProductViewMapper(LocalDate.now(ZoneId.of("UTC")))

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = ProductViewMapper.PAGE,
        event = ProductViewMapper.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun accept() {
        assertTrue(mapper.accept(track))
    }

    @Test
    fun acceptBot() {
        assertFalse(mapper.accept(track.copy(bot = true)))
    }

    @Test
    fun acceptPage() {
        assertFalse(mapper.accept(track.copy(page = "xxx")))
    }

    @Test
    fun acceptEvent() {
        assertFalse(mapper.accept(track.copy(event = "xxx")))
    }

    @Test
    fun acceptProductIdNull() {
        assertFalse(mapper.accept(track.copy(productId = null)))
    }

    @Test
    fun acceptProductIdEmpty() {
        assertFalse(mapper.accept(track.copy(productId = "")))
    }

    @Test
    fun acceptTime() {
        assertFalse(
            mapper.accept(
                track.copy(time = OffsetDateTime.now().minusDays(1).toInstant().toEpochMilli()),
            ),
        )
    }

    @Test
    fun map() {
        val result = mapper.map(track)
        assertEquals(track.productId, result?.key?.productId)
        assertEquals(1L, result?.value)
        assertEquals(track.businessId, result?.key?.businessId)
    }
}
