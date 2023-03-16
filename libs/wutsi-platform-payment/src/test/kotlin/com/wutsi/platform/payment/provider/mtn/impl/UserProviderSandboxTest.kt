package com.wutsi.platform.payment.provider.mtn.impl

import com.wutsi.platform.payment.provider.mtn.Fixtures
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class UserProviderSandboxTest {
    @Test
    fun collectionUser() {
        val user = UserProviderSandbox(
            http = Fixtures.createHttp(),
            callbackUrl = "http://127.0.0.1",
            subscriptionKey = Fixtures.COLLECTION_SUBSCRIPTION_KEY,
        ).get()

        assertNotNull(user.id)
        assertNotNull(user.apiKey)
    }

    @Test
    fun getDisbusmentUser() {
        val user = UserProviderSandbox(
            http = Fixtures.createHttp(),
            callbackUrl = "http://127.0.0.1",
            subscriptionKey = Fixtures.DISBURSEMENT_SUBSCRIPTION_KEY,
        ).get()

        assertNotNull(user.id)
        assertNotNull(user.apiKey)
    }
}
