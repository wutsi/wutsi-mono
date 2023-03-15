package com.wutsi.platform.core.tracing.spring

import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class SpringTracingRequestInterceptor(
    private val tracingContext: TracingContext,
) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        exec: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        request.headers[TracingContext.HEADER_CLIENT_ID] = listOf(tracingContext.clientId())
        request.headers[TracingContext.HEADER_TRACE_ID] = listOf(tracingContext.traceId())
        request.headers[TracingContext.HEADER_DEVICE_ID] = listOf(tracingContext.deviceId())
        tracingContext.tenantId()?.let { request.headers[TracingContext.HEADER_TENANT_ID] = listOf(it) }
        tracingContext.clientInfo()?.let { request.headers[TracingContext.HEADER_CLIENT_INFO] = listOf(it) }

        return exec.execute(request, body)
    }
}
