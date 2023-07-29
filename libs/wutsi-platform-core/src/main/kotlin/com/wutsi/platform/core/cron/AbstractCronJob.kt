package com.wutsi.platform.core.cron

import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.ThreadLocalKVLoggerHolder
import com.wutsi.platform.core.tracing.DefaultTracingContext
import com.wutsi.platform.core.tracing.ThreadLocalTracingContextHolder
import java.util.UUID
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

abstract class AbstractCronJob(
    private val lockManager: CronLockManager,
    private val registry: CronJobRegistry,
) : CronJob {

    @PostConstruct
    fun init() {
        registry.register(getJobName(), this)
    }

    @PreDestroy
    fun destroy() {
        registry.unregister(getJobName())
    }

    protected abstract fun doRun(): Long

    protected open fun getJobName(): String = this::class.java.simpleName

    override fun run() {
        // Add Logger into the ThreadLocal
        val logger = DefaultKVLogger()
        ThreadLocalKVLoggerHolder.set(logger)
        logger.add("job", getJobName())

        // Add TracingContext into the ThreadLocal
        val tc = DefaultTracingContext(
            clientId = getJobName(),
            traceId = UUID.randomUUID().toString(),
            deviceId = getJobName(),
            tenantId = null,
            clientInfo = getJobName(),
        )
        ThreadLocalTracingContextHolder.set(tc)
        logger.add("client_id", tc.clientId())
        logger.add("client_info", tc.clientInfo())
        logger.add("trace_id", tc.traceId())
        logger.add("device_id", tc.deviceId())
        logger.add("tenant_id", tc.tenantId())

        try {
            val name = getJobName()
            lockManager.lock(name)
            try {
                val result = doRun()

                logger.add("job_result", result)
                logger.add("success", true)
            } finally {
                lockManager.release(name)
            }
        } catch (ex: Exception) {
            logger.setException(ex)
            logger.add("success", false)
        } finally {
            logger.log()

            // Clear the context
            ThreadLocalTracingContextHolder.remove()
            ThreadLocalKVLoggerHolder.remove()
        }
    }
}
