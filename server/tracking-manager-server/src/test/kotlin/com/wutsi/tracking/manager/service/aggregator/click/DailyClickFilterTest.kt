package com.wutsi.tracking.manager.service.aggregator.click

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DailyClickFilterTest {
    private val filter = DailyClickFilter(LocalDate.now(ZoneId.of("UTC")))

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyClickFilter.PAGE,
        event = DailyClickFilter.EVENT,
        productId = "123",
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
    fun acceptTime() {
        assertFalse(
            filter.accept(
                track.copy(time = OffsetDateTime.now().minusDays(1).toInstant().toEpochMilli()),
            ),
        )
    }
}
