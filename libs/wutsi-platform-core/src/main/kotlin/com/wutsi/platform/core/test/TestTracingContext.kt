package com.wutsi.platform.core.test

import com.wutsi.platform.core.tracing.DefaultTracingContext
import java.util.UUID

class TestTracingContext(
    clientId: String = "test",
    traceId: String = UUID.randomUUID().toString(),
    deviceId: String = "test-device",
    tenantId: String = "1",
    clientInfo: String = "test-device.1.0.233",
) : DefaultTracingContext(clientId, traceId, deviceId, tenantId, clientInfo)
