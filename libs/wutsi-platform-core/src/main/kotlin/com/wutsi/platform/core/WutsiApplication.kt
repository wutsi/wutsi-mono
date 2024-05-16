package com.wutsi.platform.core

import com.wutsi.platform.core.cache.spring.LocalCacheConfiguration
import com.wutsi.platform.core.cache.spring.MemcachedCacheConfiguration
import com.wutsi.platform.core.cron.spring.CronJobConfiguration
import com.wutsi.platform.core.error.spring.ErrorConfiguration
import com.wutsi.platform.core.image.spring.ImageKitConfiguration
import com.wutsi.platform.core.image.spring.NoImageConfiguration
import com.wutsi.platform.core.logging.spring.LoggingConfiguration
import com.wutsi.platform.core.messaging.spring.AwsSmsConfiguration
import com.wutsi.platform.core.messaging.spring.BitlyUrlShortenerConfiguration
import com.wutsi.platform.core.messaging.spring.CloudWhatsappConfiguration
import com.wutsi.platform.core.messaging.spring.EmailMessagingConfiguration
import com.wutsi.platform.core.messaging.spring.FirebasePushNotificationConfiguration
import com.wutsi.platform.core.messaging.spring.MessagingConfiguration
import com.wutsi.platform.core.messaging.spring.NoPushNotificationConfiguration
import com.wutsi.platform.core.messaging.spring.NoSmsConfiguration
import com.wutsi.platform.core.messaging.spring.NoUrlShortenerConfiguration
import com.wutsi.platform.core.messaging.spring.NoWhatsappConfiguration
import com.wutsi.platform.core.security.spring.NoSecurityConfiguration
import com.wutsi.platform.core.security.spring.SecurityJWTConfiguration
import com.wutsi.platform.core.security.spring.TokenBlacklistNoneConfiguration
import com.wutsi.platform.core.security.spring.TokenBlacklistRedisConfiguration
import com.wutsi.platform.core.security.spring.TokenConfiguration
import com.wutsi.platform.core.storage.spring.LocalStorageConfiguration
import com.wutsi.platform.core.storage.spring.S3StorageConfiguration
import com.wutsi.platform.core.stream.spring.LocalStreamConfiguration
import com.wutsi.platform.core.stream.spring.RabbitMQStreamConfiguration
import com.wutsi.platform.core.tracing.spring.TracingConfiguration
import com.wutsi.platform.core.tracking.spring.TrackingConfiguration
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(
    value = [
        ErrorConfiguration::class,

        LocalCacheConfiguration::class,
        MemcachedCacheConfiguration::class,

        CronJobConfiguration::class,

        ImageKitConfiguration::class,
        NoImageConfiguration::class,

        LoggingConfiguration::class,

        MessagingConfiguration::class,
        EmailMessagingConfiguration::class,
        NoPushNotificationConfiguration::class,
        FirebasePushNotificationConfiguration::class,
        AwsSmsConfiguration::class,
        NoSmsConfiguration::class,
        BitlyUrlShortenerConfiguration::class,
        NoUrlShortenerConfiguration::class,
        CloudWhatsappConfiguration::class,
        NoWhatsappConfiguration::class,

        SecurityJWTConfiguration::class,
        NoSecurityConfiguration::class,

        LocalStreamConfiguration::class,
        RabbitMQStreamConfiguration::class,

        LocalStorageConfiguration::class,
        S3StorageConfiguration::class,

        TracingConfiguration::class,

        TokenConfiguration::class,
        TokenBlacklistNoneConfiguration::class,
        TokenBlacklistRedisConfiguration::class,

        TrackingConfiguration::class
    ],
)
annotation class WutsiApplication
