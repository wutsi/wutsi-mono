package com.wutsi.platform.payment.provider.mtn.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.payment.core.DefaultHttpListener
import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.provider.mtn.Environment
import com.wutsi.platform.payment.provider.mtn.Environment.PRODUCTION
import com.wutsi.platform.payment.provider.mtn.Environment.SANDBOX
import com.wutsi.platform.payment.provider.mtn.MTNGateway
import com.wutsi.platform.payment.provider.mtn.UserProvider
import com.wutsi.platform.payment.provider.mtn.impl.UserProviderProduction
import com.wutsi.platform.payment.provider.mtn.impl.UserProviderSandbox
import com.wutsi.platform.payment.provider.mtn.product.Collection
import com.wutsi.platform.payment.provider.mtn.product.Disbursement
import com.wutsi.platform.payment.provider.mtn.product.ProductConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect.NORMAL
import java.net.http.HttpClient.Version.HTTP_1_1
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.platform.payment.mtn.enabled"],
    havingValue = "true",
)
open class MTNConfiguration(
    private val objectMapper: ObjectMapper,

    @Value("\${wutsi.platform.payment.mtn.environment}") private val environment: String,
    @Value("\${wutsi.platform.payment.mtn.callback-url}") private val callbackUrl: String,
    @Value("\${wutsi.platform.payment.mtn.collection.subscription-key}") private val collectionSubscriptionKey: String,
    @Value("\${wutsi.platform.payment.mtn.collection.user-id:}") private val collectionUserId: String,
    @Value("\${wutsi.platform.payment.mtn.collection.api-key:}") private val collectionApiKey: String,
    @Value("\${wutsi.platform.payment.mtn.disbursement.subscription-key}") private val disbursementSubscriptionKey: String,
    @Value("\${wutsi.platform.payment.mtn.disbursement.user-id:}") private val disbursementUserId: String,
    @Value("\${wutsi.platform.payment.mtn.disbursement.api-key:}") private val disbursementApiKey: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MTNConfiguration::class.java)
    }

    @Bean
    open fun mtnGateway(): MTNGateway =
        MTNGateway(
            collection = mtnCollection(),
            disbursement = mtnDisbursement(),
        )

    @Bean
    open fun mtnCollection(): Collection =
        Collection(
            http = mtnHttp(),
            config = ProductConfig(
                environment = mtnEnvironment(),
                subscriptionKey = collectionSubscriptionKey,
                callbackUrl = callbackUrl,
                userProvider = createUserProvider(
                    userId = collectionUserId,
                    apiKey = collectionApiKey,
                    subscriptionKey = collectionSubscriptionKey,
                    callbackUrl = callbackUrl,
                    http = mtnHttp(),
                ),
            ),
        )

    @Bean
    open fun mtnDisbursement(): Disbursement =
        Disbursement(
            http = mtnHttp(),
            config = ProductConfig(
                environment = mtnEnvironment(),
                subscriptionKey = disbursementSubscriptionKey,
                callbackUrl = callbackUrl,
                userProvider = createUserProvider(
                    userId = disbursementUserId,
                    apiKey = disbursementApiKey,
                    subscriptionKey = disbursementSubscriptionKey,
                    callbackUrl = callbackUrl,
                    http = mtnHttp(),
                ),
            ),
        )

    @Bean
    open fun mtnCollectionHealthCheck(): HealthIndicator =
        MTNProductHealthIndicator(environment, mtnCollection())

    @Bean
    open fun mtnDisbursementHealthCheck(): HealthIndicator =
        MTNProductHealthIndicator(environment, mtnDisbursement())

    @Bean
    open fun mtnHttp(): Http {
        val env = mtnEnvironment()
        LOGGER.info("Creating HttpClient for $env")
        if (env == PRODUCTION) {
            return Http(
                client = HttpClient.newBuilder()
                    .version(HTTP_1_1)
                    .followRedirects(NORMAL)
                    .build(),
                objectMapper = objectMapper,
                listener = DefaultHttpListener(),
            )
        } else {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
            })

            val context = SSLContext.getInstance("TLS")
            context.init(null, trustAllCerts, SecureRandom())

            /*
             * TODO: This is pretty bad to set it at JDK level. How to set it per client with JDK11 HttpClient?
             */
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true")
            return Http(
                client = HttpClient.newBuilder()
                    .version(HTTP_1_1)
                    .sslContext(context)
                    .followRedirects(NORMAL)
                    .build(),
                objectMapper = objectMapper,
            )
        }
    }

    private fun createUserProvider(
        userId: String,
        apiKey: String,
        subscriptionKey: String,
        callbackUrl: String,
        http: Http,
    ): UserProvider {
        val env = mtnEnvironment()
        LOGGER.info("Creating UserProvider for $env")

        return if (env == PRODUCTION) {
            UserProviderProduction(userId, apiKey)
        } else {
            UserProviderSandbox(subscriptionKey, callbackUrl, http)
        }
    }

    private fun mtnEnvironment(): Environment =
        if (environment.equals("production", ignoreCase = true) || environment.equals("prod", ignoreCase = true)) {
            PRODUCTION
        } else {
            SANDBOX
        }
}
