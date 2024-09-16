package com.wutsi.platform.core.cache.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.cache.spring.memcached.RedisCache
import com.wutsi.platform.core.cache.spring.redis.RedisHealthIndicator
import io.lettuce.core.RedisClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
@ConditionalOnProperty(
    value = ["wutsi.platform.cache.type"],
    havingValue = "redis",
)
open class RedisCacheConfiguration(
    private val objectMapper: ObjectMapper,
    @Value("\${wutsi.platform.cache.name}") name: String,
    @Value(value = "\${wutsi.platform.cache.redis.host}") private val host: String,
    @Value(value = "\${wutsi.platform.cache.redis.port}") private val port: Int,
    @Value(value = "\${wutsi.platform.cache.redis.ttl:86400}") private val ttl: Int,
) : AbstractCacheConfiguration(name) {
    @Bean
    override fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            listOf(
                RedisCache(name, ttl, redisClient(), objectMapper),
            ),
        )
        return cacheManager
    }

    @Bean(destroyMethod = "shutdown")
    open fun redisClient(): RedisClient =
        RedisClient.create("redis://$host:$port")

    @Bean
    open fun redisHealthCheck(): HealthIndicator {
        return RedisHealthIndicator(redisClient())
    }
}
