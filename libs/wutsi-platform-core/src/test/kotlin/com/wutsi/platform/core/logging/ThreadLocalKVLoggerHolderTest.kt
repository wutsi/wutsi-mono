package com.wutsi.platform.core.logging

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ThreadLocalKVLoggerHolderTest {
    @BeforeEach
    fun setUp() {
        ThreadLocalKVLoggerHolder.remove()
    }

    @Test
    fun test() {
        assertNull(ThreadLocalKVLoggerHolder.get())

        val value = DefaultKVLogger()
        ThreadLocalKVLoggerHolder.set(value)
        assertEquals(value, ThreadLocalKVLoggerHolder.get())

        ThreadLocalKVLoggerHolder.remove()
        assertNull(ThreadLocalKVLoggerHolder.get())
    }
}
