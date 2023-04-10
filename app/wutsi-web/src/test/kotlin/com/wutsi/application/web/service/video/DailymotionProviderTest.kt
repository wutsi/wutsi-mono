package com.wutsi.application.web.service.video

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DailymotionProviderTest {
    private val service: VideoProvider = DailymotionProvider()

    @Test
    fun testGetEmbedUrl() {
        assertEquals("https://www.dailymotion.com/embed/video/123", service.generateEmbedUrl("123"))
    }

    @Test
    fun testGetVideoId() {
        val expected = "XOcCOBe8PTc"
        assertEquals(expected, service.extractVideoId("https://dailymotion.com/embed/video/XOcCOBe8PTc"))
        assertEquals(
            expected,
            service.extractVideoId("https://dailymotion.com/embed/video/XOcCOBe8PTc?syndication=131181"),
        )
        assertEquals(expected, service.extractVideoId("https://dailymotion.com/video/XOcCOBe8PTc?syndication=131181"))
        assertNull(service.extractVideoId("https://unknown.com/video/XOcCOBe8PTc?syndication=131181"))
    }
}
