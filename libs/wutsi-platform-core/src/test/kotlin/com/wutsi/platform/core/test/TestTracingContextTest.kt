package com.wutsi.platform.core.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TestTracingContextTest {
    @Test
    fun test() {
        val tc = TestTracingContext("client-id", "trace-id", "device-id", "1111")

        assertEquals("client-id", tc.clientId())
        assertEquals("device-id", tc.deviceId())
        assertEquals("trace-id", tc.traceId())
        assertEquals("1111", tc.tenantId())
    }
}
