package com.wutsi.blog.app.page.mail

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URL
import javax.imageio.ImageIO

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PixelControllerTest {
    @LocalServerPort
    private val port: Int = 0

    @MockBean
    protected lateinit var trackingBackend: TrackingBackend

    @MockBean
    protected lateinit var storyService: StoryService

    @Test
    fun `story - pixel`() {
        // WHEN
        val img = ImageIO.read(URL("http://localhost:$port/pixel/s132-u3232.png"))

        // THEN
        assertEquals(1, img.width)
        assertEquals(1, img.height)

        val req = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(req.capture())
        assertEquals("132", req.firstValue.productId)
        assertEquals("3232", req.firstValue.accountId)
        assertEquals("readstart", req.firstValue.event)
        assertEquals(PageName.READ, req.firstValue.page)
        assertEquals(PixelController.REFERER, req.firstValue.referrer)

        verify(storyService).view(132L, 3232L, 60000L)
    }

    @Test
    fun `story - ignore tracking error`() {
        // GIVEN
        doThrow(RuntimeException::class).whenever(trackingBackend).push(any())

        // WHEN
        val img = ImageIO.read(URL("http://localhost:$port/pixel/s132-u3232.png"))

        // WHEN
        assertEquals(1, img.width)
        assertEquals(1, img.height)

        verify(storyService).view(132L, 3232L, 60000L)
    }

    @Test
    fun `ads - pixel`() {
        // WHEN
        val img = ImageIO.read(URL("http://localhost:$port/ads/111/pixel/u3232.png"))

        // THEN
        assertEquals(1, img.width)
        assertEquals(1, img.height)

        val req = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(req.capture())
        assertNull(req.firstValue.productId)
        assertEquals("3232", req.firstValue.accountId)
        assertEquals("impression", req.firstValue.event)
        assertNull(req.firstValue.page)
        assertEquals(PixelController.REFERER, req.firstValue.referrer)
        assertEquals("111", req.firstValue.campaign)
        assertNull(req.firstValue.businessId)
    }

    @Test
    fun `ads - pixel for story`() {
        // WHEN
        val img = ImageIO.read(URL("http://localhost:$port/ads/111/pixel/u3232.png?s=132&b=555"))

        // THEN
        assertEquals(1, img.width)
        assertEquals(1, img.height)

        val req = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(req.capture())
        assertEquals("132", req.firstValue.productId)
        assertEquals("3232", req.firstValue.accountId)
        assertEquals("impression", req.firstValue.event)
        assertNull(req.firstValue.page)
        assertEquals(PixelController.REFERER, req.firstValue.referrer)
        assertEquals("111", req.firstValue.campaign)
        assertEquals("555", req.firstValue.businessId)
    }
}
