package com.wutsi.platform.payment.provider.mtn.product

import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.provider.mtn.model.MTNTokenResponse
import java.util.Base64

abstract class AbstractProduct(
    val config: ProductConfig,
    protected val http: Http,
) : Product {
    protected abstract fun uri(path: String): String

    override fun token(referenceId: String): MTNTokenResponse =
        http.post(
            referenceId = referenceId,
            uri = uri("token"),
            headers = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Basic " + credentials(),
                "Ocp-Apim-Subscription-Key" to config.subscriptionKey,
            ),
            requestPayload = mapOf("foo" to "bar"),
            responseType = MTNTokenResponse::class.java,
        )!!

    protected fun headers(referenceId: String?, accessToken: String) = mapOf(
        "Content-Type" to "application/json",
        "Authorization" to "Bearer $accessToken",
        "X-Callback-Url" to config.callbackUrl,
        "X-Reference-Id" to referenceId,
        "X-Target-Environment" to config.environment.name.lowercase(),
        "Ocp-Apim-Subscription-Key" to config.subscriptionKey,
    )

    private fun credentials(): String {
        val user = config.userProvider.get()
        val str = "${user.id}:${user.apiKey}"
        return Base64.getEncoder().encodeToString(str.toByteArray())
    }
}
