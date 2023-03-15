package com.wutsi.platform.core.security.spring

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.security.TokenBlacklistService
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TokenBlacklistServiceRedisTest {
    lateinit var client: RedisClient
    lateinit var connection: StatefulRedisConnection<String, String>
    lateinit var commands: RedisCommands<String, String>
    private lateinit var service: TokenBlacklistService

    private val token = "1111"
    private val key = "b59c67bf196a4758191e42f76670ceba"

    @BeforeEach
    fun setUp() {
        commands = mock()
        connection = mock()
        doReturn(commands).whenever(connection).sync()

        client = mock()
        doReturn(connection).whenever(client).connect()

        service = TokenBlacklistServiceRedis(client)
    }

    @Test
    fun add() {
        service.add(token, 111)

        verify(commands).set(key, "1")
        verify(commands).expire(key, 111L)
    }

    @Test
    fun addExpired() {
        service.add(token, -100)

        verify(commands, never()).set(any(), any())
        verify(commands, never()).expire(any(), any<Long>())
    }

    @Test
    fun containsFalse() {
        assertFalse(service.contains("222"))
    }

    @Test
    fun containsTrue() {
        doReturn("1").whenever(commands).get(key)
        assertTrue(service.contains(token))
    }
}
