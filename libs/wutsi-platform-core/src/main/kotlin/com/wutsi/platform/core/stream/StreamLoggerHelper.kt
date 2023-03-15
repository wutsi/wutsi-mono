package com.wutsi.platform.core.stream

import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext

object StreamLoggerHelper {
    fun log(event: Event, logger: KVLogger) {
        logger.add("event_id", event.id)
        logger.add("event_type", event.type)
        logger.add("event_timestamp", event.timestamp)
        logger.add("event_client_id", event.tracingData.clientId)
        logger.add("event_trace_id", event.tracingData.traceId)
        logger.add("event_device_id", event.tracingData.deviceId)
        logger.add("event_tenant_id", event.tracingData.tenantId)
    }

    fun log(tc: TracingContext, logger: KVLogger) {
        logger.add("client_id", tc.clientId())
        logger.add("trace_id", tc.traceId())
        logger.add("device_id", tc.deviceId())
        logger.add("tenant_id", tc.tenantId())
        logger.add("client_info", tc.clientInfo())
    }
}
