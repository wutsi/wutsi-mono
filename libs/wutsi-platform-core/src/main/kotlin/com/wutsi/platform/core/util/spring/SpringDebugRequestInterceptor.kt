package com.wutsi.platform.core.util.spring

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class SpringDebugRequestInterceptor : ClientHttpRequestInterceptor {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SpringDebugRequestInterceptor::class.java)
    }

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        exec: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        val startTime = System.currentTimeMillis()
        var statusText = -1
        try {
            val response = exec.execute(request, body)
            statusText = response.statusCode.value()
            return response
        } finally {
            LOGGER.info(">> ${request.method} ${request.uri} - $statusText (" + (System.currentTimeMillis() - startTime) + "ms)")
        }
    }
}
