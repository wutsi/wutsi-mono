package com.wutsi.platform.core.tracing

import com.wutsi.platform.core.test.TestTracingContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ThreadLocalTracingContextHolderTest {
    @BeforeEach
    fun setUp() {
        ThreadLocalTracingContextHolder.remove()
    }

    @Test
    fun test() {
        assertNull(ThreadLocalTracingContextHolder.get())

        val value = TestTracingContext()
        ThreadLocalTracingContextHolder.set(value)
        assertEquals(value, ThreadLocalTracingContextHolder.get())

        ThreadLocalTracingContextHolder.remove()
        assertNull(ThreadLocalTracingContextHolder.get())
    }
}
