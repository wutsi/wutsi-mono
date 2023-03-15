package com.wutsi.platform.core.stream

open class EventTracingData(
    val clientId: String = "",
    val traceId: String = "",
    val deviceId: String = "",
    val tenantId: String? = null,
    val clientInfo: String? = null,
)
