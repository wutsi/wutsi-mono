package com.wutsi.platform.core

import com.wutsi.platform.core.cache.spring.CacheConfigurationLocal
import com.wutsi.platform.core.cache.spring.CacheConfigurationMemcached
import com.wutsi.platform.core.cron.spring.CronJobConfiguration
import com.wutsi.platform.core.error.spring.ErrorConfiguration
import com.wutsi.platform.core.image.spring.ImageConfigurationImageKit
import com.wutsi.platform.core.image.spring.ImageConfigurationNone
import com.wutsi.platform.core.logging.spring.LoggingConfiguration
import com.wutsi.platform.core.messaging.spring.MessagingConfiguration
import com.wutsi.platform.core.messaging.spring.MessagingEmailConfiguration
import com.wutsi.platform.core.messaging.spring.MessagingPushConfigurationFirebase
import com.wutsi.platform.core.messaging.spring.MessagingPushConfigurationNone
import com.wutsi.platform.core.messaging.spring.MessagingSMSConfigurationAWS
import com.wutsi.platform.core.messaging.spring.MessagingSMSConfigurationNone
import com.wutsi.platform.core.messaging.spring.MessagingUrlShortenerConfigurationBitly
import com.wutsi.platform.core.messaging.spring.MessagingUrlShortenerConfigurationNone
import com.wutsi.platform.core.messaging.spring.MessagingWhatsappConfigurationCloud
import com.wutsi.platform.core.messaging.spring.MessagingWhatsappConfigurationNone
import com.wutsi.platform.core.security.spring.SecurityConfigurationJWT
import com.wutsi.platform.core.security.spring.SecurityConfigurationNone
import com.wutsi.platform.core.security.spring.TokenBlacklistConfigurationNone
import com.wutsi.platform.core.security.spring.TokenBlacklistConfigurationRedis
import com.wutsi.platform.core.security.spring.TokenConfiguration
import com.wutsi.platform.core.storage.spring.StorageConfigurationAws
import com.wutsi.platform.core.storage.spring.StorageConfigurationLocal
import com.wutsi.platform.core.stream.spring.StreamConfigurationLocal
import com.wutsi.platform.core.stream.spring.StreamConfigurationRabbitMQ
import com.wutsi.platform.core.tracing.spring.TracingConfiguration
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(
    value = [
        ErrorConfiguration::class,

        CacheConfigurationLocal::class,
        CacheConfigurationMemcached::class,

        CronJobConfiguration::class,

        ImageConfigurationImageKit::class,
        ImageConfigurationNone::class,

        LoggingConfiguration::class,

        MessagingConfiguration::class,
        MessagingEmailConfiguration::class,
        MessagingPushConfigurationNone::class,
        MessagingPushConfigurationFirebase::class,
        MessagingSMSConfigurationAWS::class,
        MessagingSMSConfigurationNone::class,
        MessagingUrlShortenerConfigurationBitly::class,
        MessagingUrlShortenerConfigurationNone::class,
        MessagingWhatsappConfigurationCloud::class,
        MessagingWhatsappConfigurationNone::class,

        SecurityConfigurationJWT::class,
        SecurityConfigurationNone::class,

        StreamConfigurationLocal::class,
        StreamConfigurationRabbitMQ::class,

        StorageConfigurationLocal::class,
        StorageConfigurationAws::class,

        TracingConfiguration::class,

        TokenConfiguration::class,
        TokenBlacklistConfigurationNone::class,
        TokenBlacklistConfigurationRedis::class,
    ],
)
annotation class WutsiApplication
