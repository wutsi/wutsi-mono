package com.wutsi.platform.core.cache.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import java.time.Duration

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
        val config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(ttl.toLong()))
            .serializeValuesWith(
                SerializationPair.fromSerializer(
                    GenericJackson2JsonRedisSerializer(objectMapper)
                )
            )

        val cf = LettuceConnectionFactory(host, port)
        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(cf)
            .cacheDefaults(config)
            .build()
    }
}
