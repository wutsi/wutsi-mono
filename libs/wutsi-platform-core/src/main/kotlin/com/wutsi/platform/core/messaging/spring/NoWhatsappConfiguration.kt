package com.wutsi.platform.core.messaging.spring

import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.whatsapp.WAMessagingService
import com.wutsi.platform.core.messaging.whatsapp.WAMessagingServiceNone
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.messaging.whatsapp.type"],
    havingValue = "none",
    matchIfMissing = true,
)
open class NoWhatsappConfiguration(
    private val provider: MessagingServiceProvider,
) {
    @Bean
    open fun whatsappMessagingService(): WAMessagingService {
        val service = WAMessagingServiceNone()
        provider.register(MessagingType.WHATSTAPP, service)
        return service
    }
}
