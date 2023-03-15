package com.wutsi.platform.core.security.spring

import com.wutsi.platform.core.security.TokenBlacklistService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.security.token-blacklist.type"],
    havingValue = "none",
    matchIfMissing = true,
)
open class TokenBlacklistConfigurationNone {
    @Bean
    open fun tokenBlackListService(): TokenBlacklistService =
        TokenBlacklistServiceNone()
}
