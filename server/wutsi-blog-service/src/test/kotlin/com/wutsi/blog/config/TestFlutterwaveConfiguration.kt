package com.wutsi.blog.config

import com.wutsi.platform.payment.provider.flutterwave.FWGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TestFlutterwaveConfiguration {
    @Bean
    @Primary
    open fun flutterwave(): FWGateway =
        TestFWGateway()
}
