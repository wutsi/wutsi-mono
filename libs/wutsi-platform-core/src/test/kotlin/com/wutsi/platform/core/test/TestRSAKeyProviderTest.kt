package com.wutsi.platform.core.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class TestRSAKeyProviderTest {
    @Test
    fun testAll() {
        val keyProvider = TestRSAKeyProvider()
        assertNotNull(keyProvider.privateKey)
        assertNotNull(keyProvider.getPublicKeyById("1"))
        assertEquals("1", keyProvider.privateKeyId)
    }
}
