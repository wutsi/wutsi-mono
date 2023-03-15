package com.wutsi.platform.core.messaging.spring

import com.wutsi.platform.core.messaging.MessagingServiceProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MessagingConfiguration {
    @Bean
    open fun messagingServiceProvider() = MessagingServiceProvider()
}
