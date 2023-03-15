package com.wutsi.platform.core.cache.spring

import com.wutsi.platform.core.cache.spring.memcached.MemcachedCache
import com.wutsi.platform.core.cache.spring.memcached.MemcachedClientBuilder
import com.wutsi.platform.core.cache.spring.memcached.MemcachedHealthIndicator
import net.rubyeye.xmemcached.MemcachedClient
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
    havingValue = "memcached",
)
open class CacheConfigurationMemcached(
    @Value("\${wutsi.platform.cache.name}") name: String,
    @Value(value = "\${wutsi.platform.cache.memcached.username}") private val username: String,
    @Value(value = "\${wutsi.platform.cache.memcached.password}") private val password: String,
    @Value(value = "\${wutsi.platform.cache.memcached.servers}") private val servers: String,
    @Value(value = "\${wutsi.platform.cache.memcached.ttl:86400}") private val ttl: Int,
) : AbstractCacheConfiguration(name) {
    @Bean
    open fun memcachedClient(): MemcachedClient =
        MemcachedClientBuilder()
            .withServers(servers)
            .withPassword(password)
            .withUsername(username)
            .build()

    @Bean
    override fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            listOf(
                MemcachedCache(name, ttl, memcachedClient()),
            ),
        )
        return cacheManager
    }

    @Bean
    open fun memcachedHealthIndicator(): HealthIndicator =
        MemcachedHealthIndicator(memcachedClient())
}
