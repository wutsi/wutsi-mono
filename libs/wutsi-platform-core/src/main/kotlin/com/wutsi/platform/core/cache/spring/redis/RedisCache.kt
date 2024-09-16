package com.wutsi.platform.core.cache.spring.memcached

import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.Cache.ValueWrapper
import org.springframework.cache.support.SimpleValueWrapper
import java.util.concurrent.Callable

class RedisCache(
    private val name: String,
    private val ttl: Int,
    private val client: RedisClient,
    private val mapper: ObjectMapper,
) : Cache {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RedisCache::class.java)
    }

    override fun getName(): String = name

    override fun getNativeCache(): Any = client

    override fun invalidate(): Boolean {
        val connection: StatefulRedisConnection<String, String> = client.connect()
        connection.use {
            val commands = connection.sync()
            commands.flushall()
        }
        return true
    }

    override fun get(key: Any): ValueWrapper? {
        val connection: StatefulRedisConnection<String, String> = client.connect()
        connection.use {
            val commands = connection.sync()
            val xkey = extendedKey(key)
            val value = commands.get(xkey)
            return value?.let { SimpleValueWrapper(value) }
        }
    }

    override fun <T : Any?> get(key: Any, clazz: Class<T>): T? {
        val value = get(key)
        return if (value != null) {
            mapper.readValue(value.get().toString(), clazz)
        } else {
            null
        }
    }

    override fun <T : Any?> get(key: Any, callable: Callable<T>): T? {
        val value = get(key)
        if (value != null) {
            return value.get() as T
        }

        val loaded = callable.call()
        if (loaded != null) {
            put(key, loaded)
            return loaded
        }
        return null
    }

    override fun put(key: Any, value: Any?) {
        val connection: StatefulRedisConnection<String, String> = client.connect()
        connection.use {
            val commands = connection.sync()
            val xkey = extendedKey(key)
            commands.set(xkey, serialize(value))
            commands.expire(xkey, ttl.toLong())
        }
    }

    override fun putIfAbsent(key: Any, value: Any?): ValueWrapper? {
        if (get(key) == null) {
            put(key, value)
        }
        return SimpleValueWrapper(value)
    }

    override fun evict(key: Any) {
        val connection: StatefulRedisConnection<String, String> = client.connect()
        connection.use {
            val commands = connection.sync()
            val xkey = extendedKey(key)
            commands.del(xkey)
        }
    }

    override fun clear() {
        invalidate()
    }

    private fun extendedKey(key: Any): String = getName() + "#$key"

    private fun serialize(value: Any?): String? {
        value ?: return null

        val type = value.javaClass
        return if (type.isPrimitive) {
            value.toString()
        } else {
            mapper.writeValueAsString(value)
        }
    }
}
