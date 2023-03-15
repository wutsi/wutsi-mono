package com.wutsi.platform.core.cache.spring.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

open class RedisHealthIndicator(
    private val client: RedisClient,
) : HealthIndicator {
    companion object {
        val KEY = "__health_check__"
    }

    override fun health(): Health {
        val start = System.currentTimeMillis()
        try {
            val connection: StatefulRedisConnection<String, String> = client.connect()
            connection.use {
                val commands = connection.sync()
                commands.get(KEY)
                return Health.up()
                    .withDetail("key", KEY)
                    .withDetail("latency", System.currentTimeMillis() - start)
                    .build()
            }
        } catch (ex: Exception) {
            return Health.down()
                .withDetail("key", KEY)
                .withDetail("latency", System.currentTimeMillis() - start)
                .withException(ex)
                .build()
        }
    }
}
