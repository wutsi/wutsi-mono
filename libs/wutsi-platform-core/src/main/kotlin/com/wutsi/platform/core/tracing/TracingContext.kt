package com.wutsi.platform.core.tracing

interface TracingContext {
    companion object {
        const val HEADER_TRACE_ID = "X-Trace-ID"
        const val HEADER_HEROKU_REQUEST_ID = "X-Request-ID"
        const val HEADER_CLIENT_ID = "X-Client-ID"
        const val HEADER_DEVICE_ID = "X-Device-ID"
        const val HEADER_TENANT_ID = "X-Tenant-ID"
        const val HEADER_CLIENT_INFO = "X-Client-Info"

        const val NONE = "NONE"
    }

    fun traceId(): String
    fun clientId(): String
    fun deviceId(): String
    fun tenantId(): String?
    fun clientInfo(): String?
}
