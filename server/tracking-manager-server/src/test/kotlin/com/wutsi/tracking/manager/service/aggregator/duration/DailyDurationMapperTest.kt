package com.wutsi.tracking.manager.service.aggregator.duration

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneId

class DailyDurationMapperTest {
    private val detector = mock<TrafficSourceDetector>()
    private val mapper = DailyDurationMapper(detector)

    private val track = Fixtures.createTrackEntity(
        bot = false,
        page = DailyReadFilter.PAGE,
        event = DailyReadFilter.EVENT,
        productId = "123",
        time = OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli(),
    )

    @BeforeEach
    fun setUp() {
        doReturn(TrafficSource.DIRECT).whenever(detector).detect(any())
    }

    @Test
    fun map() {
        val results = mapper.map(track)

        assertEquals(1, results.size)
        val result = results[0]
        assertEquals(track.productId, result.key.productId)
        assertEquals(track.correlationId, result.key.correlationId)
        assertEquals(track.time, result.value)
    }

    @Test
    fun email() {
        doReturn(TrafficSource.EMAIL).whenever(detector).detect(any())

        val results = mapper.map(track)

        assertEquals(2, results.size)
        assertEquals(track.productId, results[0].key.productId)
        assertEquals(track.correlationId, results[0].key.correlationId)
        assertEquals(track.time, results[0].value)

        assertEquals(track.productId, results[1].key.productId)
        assertEquals(track.correlationId, results[1].key.correlationId)
        assertEquals(track.time + DailyDurationMapper.EMAIL_READ_TIME_MILLIS, results[1].value)
    }
}
