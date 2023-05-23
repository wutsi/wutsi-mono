package com.wutsi.platform.core.util.spring

import com.amazonaws.services.s3.Headers
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class SpringContentTypeRequestInterceptor(
    private val contentType: String,
) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        exec: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        request.headers[Headers.CONTENT_TYPE] = listOf(contentType)

        return exec.execute(request, body)
    }
}
