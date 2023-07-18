package com.wutsi.blog.config

import com.wutsi.platform.payment.provider.flutterwave.FWGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestFlutterwaveConfiguration {
    @Bean
    open fun fwGateway(): FWGateway =
        TestFWGateway()
}
