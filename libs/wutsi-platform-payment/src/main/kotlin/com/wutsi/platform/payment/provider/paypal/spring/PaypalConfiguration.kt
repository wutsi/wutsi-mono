package com.wutsi.platform.payment.provider.paypal.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.core.HttpListener
import com.wutsi.platform.payment.provider.paypal.Paypal
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.payment.paypal.enabled"],
    havingValue = "true",
)
class PaypalConfiguration(
    private val objectMapper: ObjectMapper,
    private val httpListener: HttpListener,
    @Value("\${wutsi.platform.payment.paypal.client-id}") private val clientId: String,
    @Value("\${wutsi.platform.payment.paypal.secret-key}") private val secretKey: String,
    @Value("\${wutsi.platform.payment.paypal.test-mode:true}") private val testMode: Boolean,
) {
    @Bean
    fun paypalGateway(): Paypal =
        Paypal(http(), clientId, secretKey, testMode)

    private fun http(): Http {
        return Http(
            client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build(),
            objectMapper = objectMapper,
            listener = httpListener,
        )
    }

    @Bean
    fun paypalHealthCheck(): HealthIndicator =
        PaypalHealthIndicator(paypalGateway())
}
