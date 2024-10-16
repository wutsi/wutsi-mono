package com.wutsi.platform.core.cache.spring.memcached

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.util.ObjectMapperBuilder
import net.rubyeye.xmemcached.MemcachedClient
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.Cache.ValueWrapper
import org.springframework.cache.support.SimpleValueWrapper
import java.util.concurrent.Callable

class MemcachedCache(
    private val name: String,
    private val ttl: Int,
    private val client: MemcachedClient,
) : Cache {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MemcachedCache::class.java)
    }

    private val mapper: ObjectMapper = ObjectMapperBuilder.build()

    override fun getName(): String = name

    override fun getNativeCache(): Any = client

    override fun invalidate(): Boolean {
        client.flushAll()
        return true
    }

    override fun get(key: Any): ValueWrapper? {
        val xkey = extendedKey(key)
        try {
            val value = client.get<CacheEntry?>(xkey)
            return value?.let { SimpleValueWrapper(deserialize(it)) }
        } catch (ex: Exception) {
            LOGGER.error("Unable to resolve $xkey from Cache#$name", ex)
            return null
        }
    }

    override fun <T : Any?> get(key: Any, clazz: Class<T>): T? {
        val value = get(key)
        return if (value != null && clazz.isAssignableFrom(value.get()?.javaClass)) {
            value.get() as T
        } else {
            null
        }
    }

    override fun <T : Any?> get(key: Any, callable: Callable<T>): T? {
        val value = get(key)
        if (value != null) {
            return value.get() as T
        }

        try {
            val loaded = callable.call()
            if (loaded != null) {
                put(key, loaded)
                return loaded
            }
            return null
        } catch (ex: Exception) {
            return null
        }
    }

    override fun put(key: Any, value: Any?) {
        val xkey = extendedKey(key)
        try {
            client.set(xkey, ttl, serialize(value))
        } catch (ex: Exception) {
            LOGGER.error("Unable to put $xkey from Cache#$name", ex)
        }
    }

    override fun putIfAbsent(key: Any, value: Any?): ValueWrapper? {
        if (get(key) == null) {
            put(key, value)
        }
        return SimpleValueWrapper(value)
    }

    override fun evict(key: Any) {
        val xkey = extendedKey(key)
        try {
            client.delete(xkey)
        } catch (ex: Exception) {
            LOGGER.error("Unable to delete $xkey from Cache#$name", ex)
        }
    }

    override fun clear() {
        try {
            client.flushAll()
        } catch (ex: Exception) {
            LOGGER.error("Unable to flush from Cache#$name", ex)
        }
    }

    private fun extendedKey(key: Any): String = getName() + "#$key"

    private fun serialize(value: Any?): CacheEntry? {
        value ?: return null

        return CacheEntry(
            classname = value.javaClass.name,
            data = mapper.writeValueAsString(value),
        )
    }

    private fun deserialize(value: CacheEntry?): Any? {
        value ?: return null

        val type = Class.forName(value.classname)
        return if (type.isPrimitive) {
            value.data
        } else {
            mapper.readValue(value.data, type)
        }
    }
}
