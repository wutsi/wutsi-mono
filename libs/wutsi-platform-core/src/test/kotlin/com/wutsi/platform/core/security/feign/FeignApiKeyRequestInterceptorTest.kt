package com.wutsi.platform.core.security.feign

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import feign.RequestTemplate
import org.junit.jupiter.api.Test

class FeignApiKeyRequestInterceptorTest {
    @Test
    fun apply() {
        // GIVEN
        val apiKey = "api-key"

        // WHEN
        val interceptor = FeignApiKeyRequestInterceptor(apiKey)
        val template = mock<RequestTemplate>()
        interceptor.apply(template)

        // THEN
        verify(template).header("X-Api-Key", apiKey)
    }

    @Test
    fun `api-key empty`() {
        // GIVEN
        val apiKey = ""

        // WHEN
        val interceptor = FeignApiKeyRequestInterceptor(apiKey)
        val template = mock<RequestTemplate>()
        interceptor.apply(template)

        // THEN
        verify(template, never()).header(eq("X-Api-Key"), any<String>())
    }

    @Test
    fun `api-key null`() {
        // GIVEN
        val apiKey = null

        // WHEN
        val interceptor = FeignApiKeyRequestInterceptor(apiKey)
        val template = mock<RequestTemplate>()
        interceptor.apply(template)

        // THEN
        verify(template, never()).header(eq("X-Api-Key"), any<String>())
    }
}
