package com.wutsi.application.web.service.video

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class VimeoProviderTest {
    private val service: VideoProvider = VimeoProvider()

    @Test
    @Throws(Exception::class)
    fun testGetEmbedUrl() {
        assertEquals("https://player.vimeo.com/video/123", service.generateEmbedUrl("123"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetVideoId() {
        val expected = "100949626"
        assertEquals(expected, service.extractVideoId("https://Vimeo.com/100949626"))
        assertEquals(expected, service.extractVideoId("https://Vimeo.com/channels/112171/100949626"))
        assertEquals(expected, service.extractVideoId("http://Vimeo.com/100949626"))
        assertEquals(expected, service.extractVideoId("https://Vimeo.com/channels/112171/100949626"))
        assertNull(service.extractVideoId("https://unknown.com/video/XOcCOBe8PTc?syndication=131181"))
    }
}
