package com.wutsi.blog.config

import com.wutsi.platform.payment.provider.flutterwave.Flutterwave
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class TestFlutterwaveConfiguration {
    @Bean
    @Primary
    fun flutterwave(): Flutterwave =
        TestFWGateway()
}
