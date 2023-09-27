package com.wutsi.platform.core.messaging.spring

import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.whatsapp.WAClient
import com.wutsi.platform.core.messaging.whatsapp.WAMessagingService
import com.wutsi.platform.core.messaging.whatsapp.WAMessagingServiceCloud
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.messaging.whatsapp.type"],
    havingValue = "cloud",
)
open class CloudWhatsappConfiguration(
    private val provider: MessagingServiceProvider,

    @Value("\${wutsi.platform.messaging.whatsapp.cloud.access-token}") private val accessToken: String,
    @Value("\${wutsi.platform.messaging.whatsapp.cloud.phone-id}") private val phoneId: String,
) {
    @Bean
    open fun whatsappClient(): WAClient =
        WAClient(
            phoneId = phoneId,
            accessToken = accessToken,
            client = HttpClient.newHttpClient(),
        )

    @Bean
    open fun whatsappMessagingService(): WAMessagingService {
        val service = WAMessagingServiceCloud(whatsappClient())
        provider.register(MessagingType.WHATSTAPP, service)
        return service
    }
}
