package com.wutsi.platform.core.cache.spring.redis

import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.cache.Cache

open class RedisHealthIndicator(
    private val cache: Cache,
) : HealthIndicator {
    companion object {
        val KEY = "__health_check__"
        private val LOGGER = LoggerFactory.getLogger(RedisHealthIndicator::class.java)
    }

    override fun health(): Health {
        val start = System.currentTimeMillis()
        try {
            cache.get(KEY)
            return Health.up()
                .withDetail("key", KEY)
                .withDetail("latency", System.currentTimeMillis() - start)
                .build()
        } catch (ex: Exception) {
            LOGGER.warn("Healthcheck error", ex)
            return Health.down()
                .withDetail("key", KEY)
                .withDetail("latency", System.currentTimeMillis() - start)
                .withException(ex)
                .build()
        }
    }
}
