package com.wutsi.platform.payment.provider.flutterwave.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.payment.GatewayProvider
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.core.DefaultHttpListener
import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.provider.flutterwave.FWEncryptor
import com.wutsi.platform.payment.provider.flutterwave.FWGateway
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.net.http.HttpClient

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.payment.flutterwave.enabled"],
    havingValue = "true",
)
open class FlutterwaveConfiguration(
    private val gatewayProvider: GatewayProvider,
    private val objectMapper: ObjectMapper,
    private val env: Environment,
    @Value("\${wutsi.platform.payment.flutterwave.secret-key}") private val secretKey: String,
    @Value("\${wutsi.platform.payment.flutterwave.encryption-key}") private val encryptionKey: String,
    @Value("\${wutsi.platform.payment.flutterwave.test-mode:true}") private val testMode: Boolean,
) {
    @Bean
    open fun fwGateway(): FWGateway {
        val gateway = FWGateway(fwHttp(), secretKey, testMode, FWEncryptor(objectMapper, encryptionKey))

        // Mobile Money
        gatewayProvider.register(PaymentMethodProvider.ORANGE, gateway)
        gatewayProvider.register(PaymentMethodProvider.MTN, gateway)

        // Credit Card
        gatewayProvider.register(PaymentMethodProvider.VISA, gateway)
        gatewayProvider.register(PaymentMethodProvider.MASTERCARD, gateway)

        // Bank
        gatewayProvider.register(PaymentMethodProvider.UBA, gateway)
        gatewayProvider.register(PaymentMethodProvider.ECO, gateway)
        gatewayProvider.register(PaymentMethodProvider.AFB, gateway)
        return gateway
    }

    @Bean
    open fun fwHttp(): Http {
        return Http(
            client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build(),
            objectMapper = objectMapper,
            listener = DefaultHttpListener(),
        )
    }

    @Bean
    open fun fwHealthCheck(): HealthIndicator =
        FlutterwaveHealthIndicator(fwGateway())
}
