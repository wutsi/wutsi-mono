package com.wutsi.blog

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.cron.CronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExecuteJobCommandExecutorTest {
    private lateinit var registry: CronJobRegistry
    private lateinit var executor: ExecuteJobCommandExecutor

    @BeforeEach
    fun setUp() {
        registry = mock()
        executor = ExecuteJobCommandExecutor(registry)
    }

    @Test
    fun runJob() {
        val job = mock<CronJob>()
        doReturn(job).whenever(registry).get(any())

        executor.execute("foo")

        verify(job).run()
    }

    @Test
    fun badName() {
        val job = mock<CronJob>()
        doReturn(job).whenever(registry).get("bar")

        executor.execute("foo")

        verify(job, never()).run()
    }
}
