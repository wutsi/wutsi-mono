package com.wutsi.platform.core.cache.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.cache.spring.memcached.RedisCache
import com.wutsi.platform.core.cache.spring.redis.RedisHealthIndicator
import io.lettuce.core.AbstractRedisClient
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.resource.DefaultClientResources
import io.lettuce.core.resource.Delay
import io.lettuce.core.resource.DirContextDnsResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.concurrent.TimeUnit

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
    @Value(value = "\${wutsi.platform.cache.redis.cluster}") private val cluster: Boolean,
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

    @Bean
    open fun redisClient(): AbstractRedisClient {
        if (cluster) {
            val uri = RedisURI.Builder.redis(host)
                .withPort(port)
                .withSsl(true)
                .build()

            val clientResources = DefaultClientResources.builder()
                .reconnectDelay(
                    Delay.fullJitter(
                        Duration.ofMillis(100), // minimum 100 millisecond delay
                        Duration.ofSeconds(10), // maximum 10 second delay
                        100, TimeUnit.MILLISECONDS
                    )
                ) // 100 millisecond base
                .dnsResolver(DirContextDnsResolver())
                .build()

            return RedisClusterClient.create(clientResources, uri)
        } else {
            return RedisClient.create("redis://$host:$port")
        }
    }

    @Bean
    open fun redisHealthCheck(): HealthIndicator {
        return RedisHealthIndicator(cache())
    }
}
