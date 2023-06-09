package com.wutsi.blog.app.page.mail

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun `return pixel`() {
        ImageIO.read(URL("http://localhost:$port/pixel/s132-u3232.png"))

        val req = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(req.capture())
        assertEquals("132", req.firstValue.productId)
        assertEquals("3232", req.firstValue.accountId)
        assertEquals("readstart", req.firstValue.event)
        assertEquals(PageName.READ, req.firstValue.page)

        verify(storyService).view(132L, 3232L, 60000L)
    }
}
