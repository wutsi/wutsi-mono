package com.wutsi.platform.core.security.spring

import com.wutsi.platform.core.security.servlet.CorsFilter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean

open class AbstractWebSecurityConfiguration {
    @Bean
    @ConditionalOnProperty(
        value = ["wutsi.platform.security.cors.enabled"],
        havingValue = "true",
        matchIfMissing = true,
    )
    open fun corsFilter() = CorsFilter()
}
