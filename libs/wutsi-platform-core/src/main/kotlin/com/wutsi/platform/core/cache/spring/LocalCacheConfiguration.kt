package com.wutsi.platform.core.cache.spring

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
@ConditionalOnProperty(
    value = ["wutsi.platform.cache.type"],
    havingValue = "local",
)
open class LocalCacheConfiguration(
    @Value("\${wutsi.platform.cache.name}") name: String,
) : AbstractCacheConfiguration(name) {
    @Bean
    override fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            listOf(
                ConcurrentMapCache(name, true),
            ),
        )
        return cacheManager
    }
}
