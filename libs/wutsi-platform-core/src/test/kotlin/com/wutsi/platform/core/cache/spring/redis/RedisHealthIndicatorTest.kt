package com.wutsi.platform.core.cache.spring.redis

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.cache.spring.memcached.MemcachedHealthIndicator
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Status
import kotlin.test.assertEquals

internal class RedisHealthIndicatorTest {
    lateinit var client: RedisClient
    lateinit var connection: StatefulRedisConnection<String, String>
    lateinit var commands: RedisCommands<String, String>
    lateinit var health: HealthIndicator

    @BeforeEach
    fun setUp() {
        commands = mock()
        connection = mock()
        doReturn(commands).whenever(connection).sync()

        client = mock()
        doReturn(connection).whenever(client).connect()

        health = RedisHealthIndicator(client)
    }

    @Test
    fun up() {
        assertEquals(Status.UP, health.health().status)
    }

    @Test
    fun down() {
        doThrow(RuntimeException()).whenever(commands).get(MemcachedHealthIndicator.KEY)
        assertEquals(Status.DOWN, health.health().status)
    }
}
