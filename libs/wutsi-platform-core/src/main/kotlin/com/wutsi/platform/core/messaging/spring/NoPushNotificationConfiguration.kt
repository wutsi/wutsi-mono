package com.wutsi.platform.core.messaging.spring

import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.push.PushMessagingService
import com.wutsi.platform.core.messaging.push.PushMessagingServiceNone
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.messaging.push.type"],
    havingValue = "none",
    matchIfMissing = true,
)
open class NoPushNotificationConfiguration(
    private val provider: MessagingServiceProvider,
) {
    @Bean
    open fun pushMessagingService(): PushMessagingService {
        val service = PushMessagingServiceNone()
        provider.register(MessagingType.PUSH_NOTIFICATION, service)
        return service
    }
}
