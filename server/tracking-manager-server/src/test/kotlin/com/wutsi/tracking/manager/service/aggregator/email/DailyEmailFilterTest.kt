package com.wutsi.tracking.manager.service.aggregator.email

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class DailyEmailFilterTest {
    private val dailyFilter: DailyReadFilter = mock()
    private val detector: TrafficSourceDetector = mock()
    private val filter = DailyEmailFilter(dailyFilter, detector)

    @Test
    fun accept() {
        // GIVEN
        doReturn(true).whenever(dailyFilter).accept(any())
        doReturn(TrafficSource.EMAIL).whenever(detector).detect(any())

        // WHEN
        val result = filter.accept(TrackEntity(accountId = "1"))

        // THEN
        assertTrue(result)
    }

    @Test
    fun noAccountId() {
        // GIVEN
        doReturn(true).whenever(dailyFilter).accept(any())
        doReturn(TrafficSource.EMAIL).whenever(detector).detect(any())

        // WHEN
        val result = filter.accept(TrackEntity())

        // THEN
        assertFalse(result)
    }

    @Test
    fun notADailyRead() {
        // GIVEN
        doReturn(false).whenever(dailyFilter).accept(any())
        doReturn(TrafficSource.EMAIL).whenever(detector).detect(any())

        // WHEN
        val result = filter.accept(TrackEntity(accountId = "1"))

        // THEN
        assertFalse(result)
    }

    @ParameterizedTest
    @EnumSource(TrafficSource::class)
    fun trafficSource(source: TrafficSource) {
        doReturn(true).whenever(dailyFilter).accept(any())
        doReturn(source).whenever(detector).detect(any())

        // WHEN
        val result = filter.accept(TrackEntity(accountId = "1"))

        // THEN
        if (source == TrafficSource.EMAIL) {
            assertTrue(result)
        } else {
            assertFalse(result)
        }
    }
}
