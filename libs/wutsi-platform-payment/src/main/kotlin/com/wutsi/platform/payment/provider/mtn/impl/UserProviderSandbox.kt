package com.wutsi.platform.payment.provider.mtn.impl

import com.wutsi.platform.payment.core.Http
import com.wutsi.platform.payment.provider.mtn.Environment.SANDBOX
import com.wutsi.platform.payment.provider.mtn.User
import com.wutsi.platform.payment.provider.mtn.UserProvider
import com.wutsi.platform.payment.provider.mtn.model.MTNApiKeyResponse
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.UUID

class UserProviderSandbox(
    private val subscriptionKey: String,
    private val callbackUrl: String,
    private val http: Http,
) : UserProvider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UserProviderSandbox::class.java)
    }

    private var user: User? = null

    override fun get(): User {
        if (user == null) {
            user = createUser()
        }

        return user!!
    }

    private fun createUser(): User {
        LOGGER.info("Creating MTN Sandbox user. subscription-key=$subscriptionKey")

        // Create user
        try {
            val userId = UUID.randomUUID().toString()
            http.post(
                referenceId = userId,
                uri = uri(),
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "X-Reference-Id" to userId,
                    "Ocp-Apim-Subscription-Key" to subscriptionKey,
                ),
                requestPayload = mapOf(
                    "providerCallbackHost" to URL(callbackUrl).host,
                ),
                responseType = Any::class.java,
            )
            LOGGER.info("User#$userId created...")

            // Get API Key
            val apiKey = http.post(
                referenceId = userId,
                uri = uri("/$userId/apikey"),
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Ocp-Apim-Subscription-Key" to subscriptionKey,
                ),
                requestPayload = emptyMap<String, String>(),
                responseType = MTNApiKeyResponse::class.java,
            )!!.apiKey
            LOGGER.info("User#$userId - apiKey=$apiKey")

            return User(id = userId, apiKey = apiKey)
        } catch (ex: Throwable) {
            LOGGER.error("Unable to resolve the user", ex)
            throw ex
        }
    }

    private fun uri(path: String = ""): String =
        SANDBOX.baseUrl + "/v1_0/apiuser$path"
}
