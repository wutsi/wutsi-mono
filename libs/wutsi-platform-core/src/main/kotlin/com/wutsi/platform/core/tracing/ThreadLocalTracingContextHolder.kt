package com.wutsi.platform.core.tracing

object ThreadLocalTracingContextHolder {
    private val value: ThreadLocal<TracingContext?> = ThreadLocal()

    fun set(tracingContext: TracingContext) {
        value.set(tracingContext)
    }

    fun get(): TracingContext? =
        value.get()

    fun remove() {
        value.remove()
    }
}
