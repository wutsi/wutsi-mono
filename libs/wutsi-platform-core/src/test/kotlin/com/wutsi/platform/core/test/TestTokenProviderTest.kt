package com.wutsi.platform.core.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestTokenProviderTest {
    @Test
    fun getToken() {
        val provider = TestTokenProvider("foo")
        assertEquals("foo", provider.getToken())
    }
}
