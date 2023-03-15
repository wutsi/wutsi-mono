package com.wutsi.platform.core.logging

object ThreadLocalKVLoggerHolder {
    private val value: ThreadLocal<KVLogger?> = ThreadLocal()

    fun set(logger: KVLogger) {
        value.set(logger)
    }

    fun get(): KVLogger? =
        value.get()

    fun remove() {
        value.remove()
    }
}
