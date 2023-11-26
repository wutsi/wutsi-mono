package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.backend.TrackingBackend
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClickControllerTest {
    @LocalServerPort
    private val port: Int = 0

    @MockBean
    protected lateinit var trackingBackend: TrackingBackend

    @MockBean
    private lateinit var tracingContext: TracingContext

    @MockBean
    private lateinit var requestContext: RequestContext

    @Test
    fun `track story`() {
        // GIVEN
        doReturn("device-id").whenever(tracingContext).deviceId()
        doReturn(UserModel(id = 111)).whenever(requestContext).currentUser()

        // WHEN
        val url = URL("http://localhost:$port/wclick?story-id=111&url=" + URLEncoder.encode("https://www.google.com"))
        val cnn = url.openConnection() as HttpURLConnection
        cnn.setRequestProperty("Referer", "https://www.foo.com")

        // THEN
        val responseCode: Int = cnn.getResponseCode()
        assertEquals(responseCode, 302)
        assertEquals("https://www.google.com", cnn.getHeaderField("Location"))

        val req = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(req.capture())
        assertEquals("111", req.firstValue.productId)
        assertEquals("click", req.firstValue.event)
        assertEquals(PageName.READ, req.firstValue.page)
        assertEquals("https://www.foo.com", req.firstValue.referrer)
        assertEquals("https://www.google.com", req.firstValue.value)
        assertEquals("device-id", req.firstValue.deviceId)
        assertEquals("111", req.firstValue.accountId)
    }

    @Test
    fun `track any url`() {
        // GIVEN
        doReturn("device-id").whenever(tracingContext).deviceId()
        doReturn(null).whenever(requestContext).currentUser()

        // WHEN
        val url = URL("http://localhost:$port/wclick?url=" + URLEncoder.encode("https://www.google.com"))
        val cnn = url.openConnection() as HttpURLConnection
        cnn.setRequestProperty("Referer", "https://www.foo.com")

        // THEN
        val responseCode: Int = cnn.getResponseCode()
        assertEquals(responseCode, 302)
        assertEquals("https://www.google.com", cnn.getHeaderField("Location"))

        val req = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(req.capture())
        assertNull(req.firstValue.productId)
        assertEquals("click", req.firstValue.event)
        assertEquals(null, req.firstValue.page)
        assertEquals("https://www.foo.com", req.firstValue.referrer)
        assertEquals("https://www.google.com", req.firstValue.value)
        assertEquals("device-id", req.firstValue.deviceId)
        assertNull(req.firstValue.accountId)
    }

    @Test
    fun `ignore tracking error`() {
        // GIVEN
        doThrow(RuntimeException::class).whenever(trackingBackend).push(any())

        // WHEN
        val url = URL("http://localhost:$port/wclick?url=" + URLEncoder.encode("https://www.google.com"))
        val cnn = url.openConnection() as HttpURLConnection
        cnn.setRequestProperty("Referer", "https://www.foo.com")

        // THEN
        val responseCode: Int = cnn.getResponseCode()
        assertEquals(responseCode, 302)
        assertEquals("https://www.google.com", cnn.getHeaderField("Location"))
    }
}
