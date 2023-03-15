package com.wutsi.platform.core.url

import com.wutsi.platform.core.messaging.url.NullUrlShortener
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NullUrlShortenerTest {
    val service = NullUrlShortener()

    @Test
    fun shorten() {
        val url = "https://www.google.ca"
        assertEquals(url, service.shorten(url))
    }
}
