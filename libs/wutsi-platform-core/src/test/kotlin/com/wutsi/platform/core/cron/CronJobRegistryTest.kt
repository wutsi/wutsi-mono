package com.wutsi.platform.core.cron

import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CronJobRegistryTest {
    lateinit var registry: CronJobRegistry

    @BeforeEach
    fun setUp() {
        registry = CronJobRegistry()
    }

    @Test
    fun register() {
        val job = mock<CronJob>()
        registry.register("foo", job)
        assertEquals(job, registry.get("foo"))
    }

    @Test
    fun registerTwice() {
        val job = mock<CronJob>()
        registry.register("foo", job)
        assertThrows<IllegalStateException> {
            registry.register("foo", job)
        }
    }

    @Test
    fun unregister() {
        val job = mock<CronJob>()
        registry.register("foo", job)
        registry.unregister("foo")
        assertNull(registry.get("foo"))
    }
}
