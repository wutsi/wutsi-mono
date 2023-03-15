package com.wutsi.platform.core.messaging.spring

import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.sms.SMSMessagingService
import com.wutsi.platform.core.messaging.sms.SMSMessagingServiceNone
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.messaging.sms.type"],
    havingValue = "none",
    matchIfMissing = true,
)
open class MessagingSMSConfigurationNone(
    private val provider: MessagingServiceProvider,
) {
    @Bean
    open fun smsMessagingService(): SMSMessagingService {
        val service = SMSMessagingServiceNone()
        provider.register(MessagingType.SMS, service)
        return service
    }
}
