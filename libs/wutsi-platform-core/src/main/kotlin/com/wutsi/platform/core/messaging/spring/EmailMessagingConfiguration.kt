package com.wutsi.platform.core.messaging.spring

import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.email.EmailMessagingService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration
@ConditionalOnProperty(name = ["spring.mail.host"])
open class EmailMessagingConfiguration(
    private val mail: JavaMailSender,
    private val provider: MessagingServiceProvider,
    @Value("\${spring.mail.properties.mail.smtp.from}") private val from: String,
) {
    @Bean
    open fun emailMessagingService(): EmailMessagingService {
        val service = EmailMessagingService(mail, from)
        provider.register(MessagingType.EMAIL, service)
        return service
    }
}
