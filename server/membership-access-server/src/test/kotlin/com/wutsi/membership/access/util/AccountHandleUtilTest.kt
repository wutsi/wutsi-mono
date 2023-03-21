package com.wutsi.membership.access.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AccountHandleUtilTest {
    @Test
    fun generate() {
        assertEquals("raysponsible", AccountHandleUtil.generate("Ray Sponsible", 30))
        assertEquals("raysponsi", AccountHandleUtil.generate("Ray Sponsible", 10))
        assertEquals("raysponsible", AccountHandleUtil.generate("Ray-Sponsible!", 30))
    }
}
