package com.wutsi.application.common.dao

import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache

abstract class AbstractCacheRepository<T> {
    @Autowired
    protected lateinit var cache: Cache

    @Autowired
    protected lateinit var tracingContext: TracingContext

    fun save(data: T) {
        cache.put(getKey(), data)
    }

    fun delete() {
        cache.evict(getKey())
    }

    abstract fun get(): T

    protected fun getKey() = tracingContext.deviceId()
}
