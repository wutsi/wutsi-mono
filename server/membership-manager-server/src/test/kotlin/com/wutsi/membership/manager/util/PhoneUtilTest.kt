package com.wutsi.membership.manager.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PhoneUtilTest {
    @Test
    fun detectCountryCM() {
        assertEquals("CM", PhoneUtil.detectCountry("+2376700000010"))
    }

    @Test
    fun detectCountryUS() {
        assertEquals("US", PhoneUtil.detectCountry("+16465550000"))
    }

    @Test
    fun detectCountryCA() {
        assertEquals("CA", PhoneUtil.detectCountry("+15147580000"))
    }
}
