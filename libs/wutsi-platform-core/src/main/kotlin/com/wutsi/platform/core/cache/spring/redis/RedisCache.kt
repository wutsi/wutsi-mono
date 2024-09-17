package com.wutsi.platform.core.cache.spring.memcached

import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.AbstractRedisClient
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulConnection
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.BaseRedisCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import io.lettuce.core.cluster.api.sync.RedisClusterCommands
import org.springframework.cache.Cache
import org.springframework.cache.Cache.ValueWrapper
import org.springframework.cache.support.SimpleValueWrapper
import java.util.concurrent.Callable

class RedisCache(
    private val name: String,
    private val ttl: Int,
    private val client: AbstractRedisClient,
    private val mapper: ObjectMapper,
) : Cache {
    override fun getName(): String = name

    override fun getNativeCache(): Any = client

    override fun invalidate(): Boolean {
        val connection = connect()
        connection.use {
            val commands = sync(connection)
            flushall(commands)
        }
        return true
    }

    override fun get(key: Any): ValueWrapper? {
        val connection = connect()
        connection.use {
            val commands = sync(connection)
            val xkey = extendedKey(key)
            val value = get(xkey, commands)
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
        val connection = connect()
        connection.use {
            val commands = sync(connection)
            val xkey = extendedKey(key)
            set(xkey, serialize(value), commands)
            expire(xkey, ttl.toLong(), commands)
        }
    }

    override fun putIfAbsent(key: Any, value: Any?): ValueWrapper? {
        if (get(key) == null) {
            put(key, value)
        }
        return SimpleValueWrapper(value)
    }

    override fun evict(key: Any) {
        val connection = connect()
        connection.use {
            val commands = sync(connection)
            val xkey = extendedKey(key)
            del(xkey, commands)
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

    private fun connect(): StatefulConnection<String, String> {
        return if (client is RedisClusterClient) {
            client.connect()
        } else if (client is RedisClient) {
            client.connect()
        } else {
            throw IllegalStateException("Invalid client")
        }
    }

    private fun sync(cnn: StatefulConnection<String, String>): BaseRedisCommands<String, String> {
        return if (cnn is StatefulRedisClusterConnection) {
            cnn.sync()
        } else if (cnn is StatefulRedisConnection) {
            cnn.sync()
        } else {
            throw IllegalStateException("Invalid client")
        }
    }

    private fun flushall(cmd: BaseRedisCommands<String, String>) {
        if (cmd is RedisCommands<String, String>) {
            cmd.flushall()
        } else if (cmd is RedisClusterCommands<String, String>) {
            cmd.flushall()
        } else {
            throw IllegalStateException("Invalid client")
        }
    }

    private fun get(key: String, cmd: BaseRedisCommands<String, String>): String? {
        return if (cmd is RedisCommands<String, String>) {
            cmd.get(key)
        } else if (cmd is RedisClusterCommands<String, String>) {
            cmd.get(key)
        } else {
            throw IllegalStateException("Invalid client")
        }
    }

    private fun set(key: String, value: String?, cmd: BaseRedisCommands<String, String>): String? {
        return if (cmd is RedisCommands<String, String>) {
            cmd.set(key, value)
        } else if (cmd is RedisClusterCommands<String, String>) {
            cmd.set(key, value)
        } else {
            throw IllegalStateException("Invalid client")
        }
    }

    private fun expire(key: String, ttl: Long, cmd: BaseRedisCommands<String, String>) {
        if (cmd is RedisCommands<String, String>) {
            cmd.expire(key, ttl)
        } else if (cmd is RedisClusterCommands<String, String>) {
            cmd.expire(key, ttl)
        } else {
            throw IllegalStateException("Invalid client")
        }
    }

    private fun del(key: String, cmd: BaseRedisCommands<String, String>) {
        if (cmd is RedisCommands<String, String>) {
            cmd.del(key)
        } else if (cmd is RedisClusterCommands<String, String>) {
            cmd.del(key)
        } else {
            throw IllegalStateException("Invalid client")
        }
    }
}
