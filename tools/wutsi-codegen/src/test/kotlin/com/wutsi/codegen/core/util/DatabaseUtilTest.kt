package com.wutsi.codegen.core.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DatabaseUtilTest {
    @Test
    fun toDatabaseName() {
        assertEquals("test", DatabaseUtil.toDatabaseName("test"))
        assertEquals("test", DatabaseUtil.toDatabaseName("test-manager"))
        assertEquals("test", DatabaseUtil.toDatabaseName("test-access"))
        assertEquals("test_toto", DatabaseUtil.toDatabaseName("test-toto"))
        assertEquals("test_toto", DatabaseUtil.toDatabaseName("test-toto-manager"))
    }
}
