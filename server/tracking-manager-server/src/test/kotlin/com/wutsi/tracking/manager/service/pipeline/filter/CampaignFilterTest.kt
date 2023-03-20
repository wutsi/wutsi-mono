package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class CampaignFilterTest {
    private val filter = CampaignFilter()

    @Test
    fun `null`() {
        val track = filter.filter(createTrack(null))
        assertNull(track.campaign)
    }

    @Test
    fun empty() {
        val track = filter.filter(createTrack(""))
        assertNull(track.campaign)
    }

    @Test
    fun utmSource() {
        val track = filter.filter(createTrack("https://www.f.com?utm_campaign=foo"))
        assertEquals("foo", track.campaign)
    }

    private fun createTrack(url: String?) = TrackEntity(
        url = url,
    )
}
