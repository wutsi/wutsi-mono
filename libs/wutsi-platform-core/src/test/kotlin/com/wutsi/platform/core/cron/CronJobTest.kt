package com.wutsi.platform.core.cron

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

internal class CronJobTest {
    private lateinit var lockManager: CronLockManager
    private lateinit var job: Job

    @BeforeEach
    fun setUp() {
        lockManager = mock { }
        job = Job(lockManager)
    }

    @Test
    fun run() {
        job.run()

        assertTrue(job.runned)
        verify(lockManager).lock(any())
        verify(lockManager).release(any())
    }

    @Test
    fun locked() {
        doThrow(LockException::class).whenever(lockManager).lock(any())

        job.run()

        assertFalse(job.runned)
        verify(lockManager, never()).release(any())
    }
}

class Job(lockManager: CronLockManager) : AbstractCronJob(lockManager) {
    var runned: Boolean = false

    override fun getJobName() = "Job"

    override fun doRun(): Long {
        runned = true
        return 1L
    }
}
