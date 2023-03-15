package com.wutsi.platform.core.stream

import java.time.OffsetDateTime
import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val type: String = "",
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val payload: String = "",
    val tracingData: EventTracingData = EventTracingData(),
)
