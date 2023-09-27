package com.wutsi.platform.core.messaging

import com.wutsi.platform.core.messaging.spring.AwsSmsConfiguration
import com.wutsi.platform.core.messaging.spring.EmailMessagingConfiguration
import com.wutsi.platform.core.messaging.spring.MessagingConfiguration
import com.wutsi.platform.core.messaging.spring.NoSmsConfiguration
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(
    value = [
        MessagingConfiguration::class,
        EmailMessagingConfiguration::class,
        MessagingServiceProvider::class,
        AwsSmsConfiguration::class,
        NoSmsConfiguration::class,
    ],
)
annotation class WutsiMessaging
