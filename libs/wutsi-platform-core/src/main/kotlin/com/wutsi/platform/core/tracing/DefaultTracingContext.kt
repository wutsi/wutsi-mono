package com.wutsi.platform.core.tracing

import java.util.UUID

open class DefaultTracingContext(
    private val clientId: String,
    private val traceId: String = UUID.randomUUID().toString(),
    private val deviceId: String,
    private val tenantId: String?,
    private val clientInfo: String?,
) : TracingContext {
    override fun clientId(): String = clientId
    override fun traceId(): String = traceId
    override fun deviceId(): String = deviceId
    override fun tenantId(): String? = tenantId
    override fun clientInfo(): String? = clientInfo
}
