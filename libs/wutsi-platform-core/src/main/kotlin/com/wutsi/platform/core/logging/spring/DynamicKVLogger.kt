package com.wutsi.platform.core.logging.spring

import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.logging.ThreadLocalKVLoggerHolder
import org.springframework.context.ApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import java.util.Optional

open class DynamicKVLogger(
    private val context: ApplicationContext,
) : KVLogger {
    override fun log() {
        getLogger()?.log()
    }

    override fun setException(ex: Throwable) {
        getLogger()?.setException(ex)
    }

    override fun add(key: String, value: String?) {
        getLogger()?.add(key, value)
    }

    override fun add(key: String, value: Long?) {
        getLogger()?.add(key, value)
    }

    override fun add(key: String, value: Double?) {
        getLogger()?.add(key, value)
    }

    override fun add(key: String, value: Optional<Any>) {
        getLogger()?.add(key, value)
    }

    override fun add(key: String, values: Collection<Any>?) {
        getLogger()?.add(key, values)
    }

    override fun add(key: String, value: Any?) {
        getLogger()?.add(key, value)
    }

    private fun getLogger(): KVLogger? =
        if (RequestContextHolder.getRequestAttributes() != null) {
            context.getBean(DefaultKVLogger::class.java)
        } else {
            ThreadLocalKVLoggerHolder.get()
        }
}
