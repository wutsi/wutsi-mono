package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

class DailyDurationFilterTest {
    private val filter = DailyDurationFilter(LocalDate.now(ZoneId.of("UTC")))

    private var track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyReadFilter.PAGE,
        event = DailyReadFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @Test
    fun readEndEvent() {
        assertTrue(filter.accept(track.copy(event = "readend")))
    }

    @Test
    fun readEndEventTomorrow() {
        assertTrue(
            filter.accept(
                track.copy(
                    event = "readend",
                    time = OffsetDateTime.now().plusDays(1).toInstant().toEpochMilli()
                )
            )
        )
    }

    @Test
    fun readEndEventYesterday() {
        assertFalse(
            filter.accept(
                track.copy(
                    event = "readend",
                    time = OffsetDateTime.now().minusDays(1).toInstant().toEpochMilli()
                )
            )
        )
    }

    @Test
    fun accept() {
        assertTrue(filter.accept(track))
    }

    @Test
    fun bot() {
        assertFalse(filter.accept(track.copy(bot = true)))
    }

    @Test
    fun invalidPage() {
        assertFalse(filter.accept(track.copy(page = "xxx")))
    }

    @Test
    fun invalidEvent() {
        assertFalse(filter.accept(track.copy(event = "xxx")))
    }

    @Test
    fun productIdNull() {
        assertFalse(filter.accept(track.copy(productId = null)))
    }

    @Test
    fun productIdEmpty() {
        assertFalse(filter.accept(track.copy(productId = "")))
    }

    @Test
    fun correlationIdNull() {
        assertFalse(filter.accept(track.copy(correlationId = null)))
    }

    @Test
    fun correlationIdEmpty() {
        assertFalse(filter.accept(track.copy(correlationId = "")))
    }

    @Test
    fun yesterday() {
        assertFalse(
            filter.accept(
                track.copy(time = OffsetDateTime.now().minusDays(1).toInstant().toEpochMilli()),
            ),
        )
    }
}
