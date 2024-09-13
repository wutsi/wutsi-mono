package com.wutsi.platform.core.cache.spring.memcached

import net.rubyeye.xmemcached.MemcachedClient
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

open class MemcachedHealthIndicator(
    private val client: MemcachedClient,
) : HealthIndicator {
    companion object {
        val KEY = "__health_check__"
        private val LOGGER = LoggerFactory.getLogger(MemcachedHealthIndicator::class.java)
    }

    override fun health(): Health {
        val start = System.currentTimeMillis()
        try {
            client.get<String>(KEY)
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
