package com.wutsi.platform.core.cache.spring.memcached

import java.io.Serializable

data class CacheEntry(
    val classname: String,
    val data: String,
) : Serializable
