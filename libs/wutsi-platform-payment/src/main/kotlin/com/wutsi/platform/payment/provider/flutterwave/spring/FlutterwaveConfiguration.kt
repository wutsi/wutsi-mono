package com.wutsi.platform.payment.provider.flutterwave.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.payment.core.DefaultHttpListener
import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.provider.flutterwave.FWGateway
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.payment.flutterwave.enabled"],
    havingValue = "true",
)
open class FlutterwaveConfiguration(
    private val objectMapper: ObjectMapper,
    @Value("\${wutsi.platform.payment.flutterwave.secret-key}") private val secretKey: String,
    @Value("\${wutsi.platform.payment.flutterwave.test-mode:true}") private val testMode: Boolean,
) {
    @Bean
    open fun fwGateway(): FWGateway =
        FWGateway(fwHttp(), secretKey, testMode)

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
