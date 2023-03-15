package com.wutsi.platform.core.logging

import java.util.Optional

interface KVLogger {
    fun log()
    fun add(key: String, value: String?)
    fun add(key: String, value: Long?)
    fun add(key: String, value: Double?)
    fun add(key: String, value: Optional<Any>)
    fun add(key: String, values: Collection<Any>?)
    fun add(key: String, value: Any?)
    fun setException(ex: Throwable)
}
