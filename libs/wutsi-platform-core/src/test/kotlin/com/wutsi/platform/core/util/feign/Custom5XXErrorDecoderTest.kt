package com.wutsi.platform.core.util.feign

import feign.FeignException
import feign.Request
import feign.RequestTemplate
import feign.Response
import feign.RetryableException
import kotlin.test.Test
import kotlin.test.assertTrue

internal class Custom5XXErrorDecoderTest {
    private val decoder = Custom5XXErrorDecoder()

    @Test
    fun error503() {
        val request = createRequest()
        val response = createResponse(503, request)

        val ex = decoder.decode("x", response)
        assertTrue(ex is RetryableException)
    }

    @Test
    fun error504() {
        val request = createRequest()
        val response = createResponse(503, request)

        val ex = decoder.decode("x", response)
        assertTrue(ex is RetryableException)
    }

    @Test
    fun error500() {
        val request = createRequest()
        val response = createResponse(500, request)

        val ex = decoder.decode("x", response)
        assertTrue(ex is FeignException.InternalServerError)
    }

    @Test
    fun error400() {
        val request = createRequest()
        val response = createResponse(400, request)

        val ex = decoder.decode("x", response)
        assertTrue(ex is FeignException.BadRequest)
    }

    private fun createRequest(): Request =
        Request.create(
            Request.HttpMethod.POST,
            "https://www.google.com",
            emptyMap(),
            Request.Body.create(""),
            RequestTemplate(),
        )

    private fun createResponse(status: Int, request: Request): Response =
        Response.builder()
            .request(request)
            .reason("This is reason")
            .status(status)
            .build()
}
