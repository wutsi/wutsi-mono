package com.wutsi.blog.subscription.service.validator

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmailDomainValidatorTest {
    private val validator = EmailDomainValidator(
        listOf(
            "gmail.com",
            "yahoo.com",
            "yahoo.fr"
        )
    )

    @Test
    fun validate() {
        assertTrue(validator.validate("ray@gmail.com"))
        assertTrue(validator.validate("ray@yahoo.com"))
        assertTrue(validator.validate(""))
        assertFalse(validator.validate("foo@hotmail.com"))
    }
}
