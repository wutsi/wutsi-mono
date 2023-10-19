package com.wutsi.tracking.manager.service.aggregator.source

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.tracking.manager.Fixtures.createTrackEntity
import com.wutsi.tracking.manager.dto.ChannelType
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DailySourceMapperTest {
    private val detector: TrafficSourceDetector = mock()
    private val mapper = DailySourceMapper(detector)

    @Test
    fun map() {
        doReturn(TrafficSource.EMAIL).whenever(detector).detect(any())

        val track = createTrackEntity(channel = ChannelType.SEO)
        val result = mapper.map(track)

        assertEquals(track.productId, result.key.productId)
        assertEquals(TrafficSource.EMAIL, result.key.source)
        assertEquals(1L, result.value)
    }
}
