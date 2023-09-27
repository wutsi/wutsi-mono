package com.wutsi.platform.core.messaging.spring

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.push.PushMessagingService
import com.wutsi.platform.core.messaging.push.PushMessagingServiceFirebase
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.messaging.push.type"],
    havingValue = "firebase",
)
open class FirebasePushNotificationConfiguration(
    private val provider: MessagingServiceProvider,
    @Value("\${wutsi.platform.messaging.push.firebase.credentials}") private val credentials: String,
) {
    @Bean
    open fun firebaseApp(): FirebaseApp {
        if (FirebaseApp.getApps().isEmpty()) {
            val input = ByteArrayInputStream(credentials.toByteArray())
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(input))
                .build()
            FirebaseApp.initializeApp(options)
        }
        return FirebaseApp.getInstance()
    }

    @Bean
    open fun firebaseMessaging(): FirebaseMessaging =
        FirebaseMessaging.getInstance(firebaseApp())

    @Bean
    open fun pushMessagingService(): PushMessagingService {
        val service = PushMessagingServiceFirebase(firebaseMessaging())
        provider.register(MessagingType.PUSH_NOTIFICATION, service)
        return service
    }
}
