package com.wutsi.blog.subscription.service.validator

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmailRoleValidatorTest {
    private val validator = EmailRoleValidator(
        listOf(
            "sales",
            "no-reply"
        )
    )

    @Test
    fun validate() {
        assertTrue(validator.validate("ray@sponsible.com"))
        assertTrue(validator.validate(""))
        assertFalse(validator.validate("sales@gmail.com"))
        assertFalse(validator.validate("NO-reply@gmail.com"))
    }
}
