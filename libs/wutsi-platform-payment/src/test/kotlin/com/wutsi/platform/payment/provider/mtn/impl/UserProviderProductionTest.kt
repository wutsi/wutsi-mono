package com.wutsi.platform.payment.provider.mtn.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UserProviderProductionTest {
    @Test
    fun get() {
        val user = UserProviderProduction("a", "b").get()

        assertEquals("a", user.id)
        assertEquals("b", user.apiKey)
    }
}
