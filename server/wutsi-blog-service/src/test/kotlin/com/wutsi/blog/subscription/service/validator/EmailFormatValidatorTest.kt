package com.wutsi.blog.subscription.service.validator

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmailFormatValidatorTest {
    private val validator = EmailFormatValidator()

    @Test
    fun validate() {
        assertTrue(validator.validate("ray@sponsible.com"))
        assertFalse(validator.validate("ray"))
        assertFalse(validator.validate(""))
    }
}
