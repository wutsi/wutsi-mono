package com.wutsi.platform.core.util

import com.nhaarman.mockitokotlin2.mock
import org.hibernate.boot.model.naming.Identifier
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CustomPhysicalNamingStrategyTest {
    private val strategy = CustomPhysicalNamingStrategy()

    @Test
    fun toPhysicalTableName() {
        val id = Identifier("T_USER", false)
        val ctx = mock<JdbcEnvironment>()

        val xid = strategy.toPhysicalTableName(id, ctx)
        assertEquals(id.text, xid.text)
        assertEquals(id.isQuoted, xid.isQuoted)
    }
}
