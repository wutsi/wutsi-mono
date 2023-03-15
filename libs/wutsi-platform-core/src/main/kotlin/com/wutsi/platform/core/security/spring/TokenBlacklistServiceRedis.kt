package com.wutsi.platform.core.security.spring

import com.wutsi.platform.core.security.TokenBlacklistService
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import org.apache.commons.codec.digest.DigestUtils

class TokenBlacklistServiceRedis(private val client: RedisClient) : TokenBlacklistService {
    override fun add(token: String, ttl: Long) {
        if (ttl <= 0) {
            return
        }

        val connection: StatefulRedisConnection<String, String> = client.connect()
        connection.use {
            val commands = connection.sync()
            val key = toKey(token)
            commands.set(key, "1")
            commands.expire(key, ttl)
        }
    }

    override fun contains(token: String): Boolean {
        val connection: StatefulRedisConnection<String, String> = client.connect()
        connection.use {
            val commands = connection.sync()
            val key = toKey(token)
            return commands.get(key) != null
        }
    }

    private fun toKey(token: String): String =
        DigestUtils.md5Hex(token)
}
