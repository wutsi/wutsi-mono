package com.wutsi.tracking.manager.service.pipeline.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SourceFilterTest {
    private val detector: TrafficSourceDetector = mock()
    private val filter = SourceFilter(detector)

    @Test
    fun filter() {
        doReturn(TrafficSource.EMAIL).whenever(detector).detect(any())

        val track = filter.filter(TrackEntity())
        assertEquals(TrafficSource.EMAIL.name, track.source)
    }
}
