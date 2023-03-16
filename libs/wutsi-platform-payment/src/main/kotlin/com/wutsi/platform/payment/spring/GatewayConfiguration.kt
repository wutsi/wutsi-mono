package com.wutsi.platform.payment.spring

import com.wutsi.platform.payment.GatewayProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class GatewayConfiguration {
    @Bean
    open fun gatewayProvider(): GatewayProvider =
        GatewayProvider()
}
