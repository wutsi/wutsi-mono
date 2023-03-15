package com.wutsi.platform.core.tracing.spring

import com.wutsi.platform.core.tracing.ThreadLocalTracingContextHolder
import com.wutsi.platform.core.tracing.TracingContext
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import java.util.UUID

open class DynamicTracingContext(private val context: ApplicationContext) : TracingContext {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DynamicTracingContext::class.java)
    }

    override fun traceId(): String {
        val tc = get()
        if (tc == null) {
            LOGGER.debug("No TracingContext available in Application Context")
            return UUID.randomUUID().toString()
        } else {
            return tc.traceId()
        }
    }

    override fun clientId() = get()?.clientId() ?: TracingContext.NONE
    override fun deviceId() = get()?.deviceId() ?: TracingContext.NONE
    override fun tenantId() = get()?.tenantId()
    override fun clientInfo(): String? = get()?.clientInfo()

    private fun get(): TracingContext? =
        if (RequestContextHolder.getRequestAttributes() != null) {
            context.getBean(RequestTracingContext::class.java)
        } else {
            ThreadLocalTracingContextHolder.get()
        }
}
