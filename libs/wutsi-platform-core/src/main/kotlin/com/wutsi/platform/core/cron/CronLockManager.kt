package com.wutsi.platform.core.cron

import org.apache.commons.codec.digest.DigestUtils
import org.springframework.cache.Cache

open class CronLockManager(private val cache: Cache) {
    open fun lock(name: String) {
        val key = getKey(name)
        if (cache.get(key) != null) {
            throw LockException(name)
        }
        cache.put(key, "1")
    }

    open fun release(name: String) {
        cache.evict(getKey(name))
    }

    private fun getKey(name: String): String =
        DigestUtils.md5Hex("com.wutsi.cron.job.$name")
}
