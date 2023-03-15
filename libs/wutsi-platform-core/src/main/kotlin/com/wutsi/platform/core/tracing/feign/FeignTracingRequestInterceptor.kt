package com.wutsi.platform.core.tracing.feign

import com.wutsi.platform.core.tracing.TracingContext
import feign.RequestInterceptor
import feign.RequestTemplate

class FeignTracingRequestInterceptor(
    private val clientId: String,
    private val tracingContext: TracingContext,
) : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        val traceId = tracingContext.traceId()

        template.header(TracingContext.HEADER_CLIENT_ID, clientId)
        template.header(TracingContext.HEADER_DEVICE_ID, tracingContext.deviceId())
        template.header(TracingContext.HEADER_TRACE_ID, traceId)
        tracingContext.tenantId()?.let { template.header(TracingContext.HEADER_TENANT_ID, it) }
        tracingContext.clientInfo()?.let { template.header(TracingContext.HEADER_CLIENT_INFO, it) }

        /**
         * For Heroku integration. See https://devcenter.heroku.com/articles/http-request-id
         */
        template.header(TracingContext.HEADER_HEROKU_REQUEST_ID, traceId)
    }
}
