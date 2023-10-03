package com.wutsi.platform.core.url

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.messaging.url.BitlyUrlShortener
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.assertEquals

internal class BitlyUrlShortenerTest {
    @Test
    fun shorten() {
        val service = BitlyUrlShortener("7c6a88dd1ca7633b0d5e15336184848e0ec5d22c", ObjectMapper())

        val short = service.shorten("https://www.google.ca")
        assertEquals("https://bit.ly/2KPbcAE", short)
    }

    @Test
    fun invalidToken() {
        val service = BitlyUrlShortener("xxxx", ObjectMapper())

        val short = service.shorten("https://www.google.ca")
        assertEquals("https://www.google.ca", short)
    }

    @Test
    fun serviceError() {
        val http = mock<HttpClient>()
        doThrow(RuntimeException::class).whenever(http).send(any<HttpRequest>(), any<HttpResponse.BodyHandler<*>>())

        val service = BitlyUrlShortener("7c6a88dd1ca7633b0d5e15336184848e0ec5d22c", ObjectMapper(), http)

        val short = service.shorten("https://www.google.ca")
        assertEquals("https://www.google.ca", short)
    }
}
