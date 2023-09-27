package com.wutsi.platform.core.messaging.spring

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClient
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.UrlShortener
import com.wutsi.platform.core.messaging.sms.SMSMessagingService
import com.wutsi.platform.core.messaging.sms.SMSMessagingServiceAWS
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.messaging.sms.type"],
    havingValue = "aws",
)
open class AwsSmsConfiguration(
    private val provider: MessagingServiceProvider,
    private val urlShortener: UrlShortener,
    @Value("\${wutsi.platform.messaging.sms.aws.region}") private val region: String,
) {
    @Bean
    open fun amazonSNS(): AmazonSNS =
        AmazonSNSClient.builder()
            .withRegion(region)
            .build()

    @Bean
    open fun smsMessagingService(): SMSMessagingService {
        val service = SMSMessagingServiceAWS(amazonSNS(), urlShortener)
        provider.register(MessagingType.SMS, service)
        return service
    }
}
