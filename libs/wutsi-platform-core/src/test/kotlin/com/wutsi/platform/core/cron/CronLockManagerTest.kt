package com.wutsi.platform.core.cron

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.cache.Cache

internal class CronLockManagerTest {
    private lateinit var cache: Cache
    private lateinit var lockManager: CronLockManager

    @BeforeEach
    fun setUp() {
        cache = mock()
        lockManager = CronLockManager(cache)
    }

    @Test
    fun lock() {
        lockManager.lock("yo")
    }

    @Test
    fun alreadyLocked() {
        doReturn(Cache.ValueWrapper { "1" }).whenever(cache).get(any())

        assertThrows<LockException> {
            lockManager.lock("man")
        }
    }

    @Test
    fun release() {
        lockManager.release("yo")

        verify(cache).evict(any())
    }
}
