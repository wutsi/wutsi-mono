package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

class DailyViewFilterTest {
    private val filter = DailyViewFilter(LocalDate.now(ZoneId.of("UTC")))

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyViewFilter.PAGE,
        event = DailyViewFilter.EVENT,
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
