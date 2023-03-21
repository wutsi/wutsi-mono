package com.wutsi.marketplace.access.endpoint

import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestTemplate

abstract class AbstractLanguageAwareControllerTest : ClientHttpRequestInterceptor {
    protected val rest = RestTemplate()
    protected var language: String? = null

    @BeforeEach
    fun setUp() {
        language = null
        rest.interceptors = listOf(this)
    }

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        if (language != null) {
            request.headers.add("Accept-Language", language)
        }
        return execution.execute(request, body)
    }
}
