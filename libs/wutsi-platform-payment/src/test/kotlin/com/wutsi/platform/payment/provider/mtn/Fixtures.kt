package com.wutsi.platform.payment.provider.mtn

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.provider.mtn.Environment.SANDBOX
import com.wutsi.platform.payment.provider.mtn.impl.UserProviderSandbox
import com.wutsi.platform.payment.provider.mtn.product.ProductConfig
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect.NORMAL
import java.net.http.HttpClient.Version.HTTP_1_1
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object Fixtures {
    const val CALLBACK_URL = "http://127.0.0.1/mtn/callback"
    const val COLLECTION_SUBSCRIPTION_KEY = "8f69aa83ec244a82b9b3006dbb91dead" // h...@gmail.com
    const val DISBURSEMENT_SUBSCRIPTION_KEY = "575487abb6b44f508eb102d5b002dd69" // h...@gmail.com
    const val NUMBER_PENDING = "46733123454"
    const val NUMBER_TIMEOUT = "46733123452"
    const val NUMBER_REJECTED = "46733123451"
    const val NUMBER_FAILED = "46733123450"
    const val NUMBER_SUCCESS = "+237221234100"

    fun createDisbursementConfig(): ProductConfig =
        createConfig(DISBURSEMENT_SUBSCRIPTION_KEY)

    fun createCollectionConfig(): ProductConfig =
        createConfig(COLLECTION_SUBSCRIPTION_KEY)

    private fun createConfig(subscriptionKey: String): ProductConfig {
        return createMtnConfig(subscriptionKey)
    }

    private fun createMtnConfig(subscriptionKey: String) =
        ProductConfig(
            environment = SANDBOX,
            subscriptionKey = subscriptionKey,
            userProvider = UserProviderSandbox(subscriptionKey, CALLBACK_URL, createHttp()),
            callbackUrl = "http://127.0.0.1/callback",
        )

    fun createHttp(): Http {
        HttpsURLConnection.setDefaultHostnameVerifier { hostname, sslSession ->
            false
        }

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
        })

        val context = SSLContext.getInstance("TLS")
        context.init(null, trustAllCerts, SecureRandom())

        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", "true")

        return Http(
            client = HttpClient.newBuilder()
                .version(HTTP_1_1)
                .sslContext(context)
                .followRedirects(NORMAL)
                .build(),
            objectMapper = ObjectMapper(),
        )
    }
}
