package com.wutsi.tracking.manager.service.pipeline.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.enums.ChannelType
import com.wutsi.enums.util.ChannelDetector
import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ChannelFilterTest {
    private val detector = mock<ChannelDetector>()
    private val filter = ChannelFilter(detector)

    @Test
    fun noUrl() {
        val track = filter.filter(createTrack(url = null))
        assertEquals(ChannelType.UNKNOWN.name, track.channel)
    }

    @Test
    fun filter() {
        doReturn(ChannelType.SEO).whenever(detector).detect(any(), any(), any())

        val track = filter.filter(createTrack(url = "http://www.google.com"))
        assertEquals(ChannelType.SEO.name, track.channel)
    }

    private fun createTrack(
        url: String? = null,
        referrer: String? = null,
        ua: String? = null,
    ) = TrackEntity(
        url = url,
        referrer = referrer,
        ua = ua,
    )
}
