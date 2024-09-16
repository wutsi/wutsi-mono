package com.wutsi.platform.core.security.spring

import com.wutsi.platform.core.cache.spring.redis.RedisHealthIndicator
import com.wutsi.platform.core.security.TokenBlacklistService
import io.lettuce.core.RedisClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.security.token-blacklist.type"],
    havingValue = "redis",
)
open class TokenBlacklistRedisConfiguration(
    @Value("\${wutsi.platform.security.token-blacklist.redis.url}") private val url: String,
    private val client: RedisClient
) {
    @Bean
    open fun tokenBlackListService(): TokenBlacklistService =
        TokenBlacklistServiceRedis(client)

    @Bean
    open fun tokenBlacklistHealthCheck(): HealthIndicator =
        RedisHealthIndicator(client)
}
