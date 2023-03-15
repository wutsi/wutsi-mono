package com.wutsi.platform.core.messaging

import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class MessagingServiceProviderTest {
    @Test
    fun get() {
        val provider = MessagingServiceProvider()
        val service = mock<MessagingService>()

        provider.register(MessagingType.EMAIL, service)
        assertEquals(service, provider.get(MessagingType.EMAIL))
    }

    @Test
    fun notFound() {
        val provider = MessagingServiceProvider()

        assertThrows<IllegalStateException> {
            provider.get(MessagingType.EMAIL)
        }
    }
}
