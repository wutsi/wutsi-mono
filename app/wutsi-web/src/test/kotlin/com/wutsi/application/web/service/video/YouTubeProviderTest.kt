package com.wutsi.application.web.service.video

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class YouTubeProviderTest {
    private val service: VideoProvider = YouTubeProvider()

    @Test
    fun testGetEmbedUrl() {
        assertEquals("https://www.youtube.com/embed/123", service.generateEmbedUrl("123"))
    }

    @Test
    fun testGetVideoId() {
        val expected = "XOcCOBe8PTc"
        assertEquals(expected, service.extractVideoId("https://www.youtube.com/embed/XOcCOBe8PTc"))
        assertEquals(expected, service.extractVideoId("http://www.youtube.com/embed/XOcCOBe8PTc"))
        assertEquals(
            expected,
            service.extractVideoId("https://www.youtube.com/embed/XOcCOBe8PTc?hd=1&rel=0&wmode=transparent"),
        )
        assertEquals(
            expected,
            service.extractVideoId("https://www.youtube-nocookie.com/embed/XOcCOBe8PTc?hd=1&wmode=opaque&rel=0"),
        )
        assertEquals(
            expected,
            service.extractVideoId("https://www.youtube-nocookie.com/embed/XOcCOBe8PTc?hd=1&rel=0&wmode=transparent"),
        )
        assertEquals(expected, service.extractVideoId("https://www.youtube.com/watch?v=XOcCOBe8PTc"))
        assertEquals(expected, service.extractVideoId("http://www.youtube.com/watch?v=XOcCOBe8PTc"))
        assertEquals(expected, service.extractVideoId("https://youtu.be/XOcCOBe8PTc"))
        assertEquals(expected, service.extractVideoId("http://youtu.be/XOcCOBe8PTc"))
        assertNull(service.extractVideoId("https://www.youtube.com/user/12121"))
    }
}
