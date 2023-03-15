package com.wutsi.platform.core.messaging

import com.wutsi.platform.core.messaging.spring.MessagingConfiguration
import com.wutsi.platform.core.messaging.spring.MessagingEmailConfiguration
import com.wutsi.platform.core.messaging.spring.MessagingSMSConfigurationAWS
import com.wutsi.platform.core.messaging.spring.MessagingSMSConfigurationNone
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(
    value = [
        MessagingConfiguration::class,
        MessagingEmailConfiguration::class,
        MessagingServiceProvider::class,
        MessagingSMSConfigurationAWS::class,
        MessagingSMSConfigurationNone::class,
    ],
)
annotation class WutsiMessaging
