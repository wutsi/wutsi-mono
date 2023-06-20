package com.wutsi.tracking.manager.service.aggregator.scrolls

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DailyScrollFilterTest {
    private val filter = DailyScrollFilter(LocalDate.now(ZoneId.of("UTC")))

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyScrollFilter.PAGE,
        event = DailyScrollFilter.EVENT,
        productId = "123",
        value = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun accept() {
        assertTrue(filter.accept(track))
    }

    @Test
    fun acceptBot() {
        assertFalse(filter.accept(track.copy(bot = true)))
    }

    @Test
    fun acceptPage() {
        assertFalse(filter.accept(track.copy(page = "xxx")))
    }

    @Test
    fun acceptEvent() {
        assertFalse(filter.accept(track.copy(event = "xxx")))
    }

    @Test
    fun acceptProductIdNull() {
        assertFalse(filter.accept(track.copy(productId = null)))
    }

    @Test
    fun acceptProductIdEmpty() {
        assertFalse(filter.accept(track.copy(productId = "")))
    }

    @Test
    fun acceptValueEmpty() {
        assertFalse(filter.accept(track.copy(value = "")))
    }

    @Test
    fun acceptValueNull() {
        assertFalse(filter.accept(track.copy(value = null)))
    }

    @Test
    fun acceptValueNotNumeric() {
        assertFalse(filter.accept(track.copy(value = "xxx")))
    }

    @Test
    fun acceptTime() {
        assertFalse(
            filter.accept(
                track.copy(time = OffsetDateTime.now().minusDays(1).toInstant().toEpochMilli()),
            ),
        )
    }
}
