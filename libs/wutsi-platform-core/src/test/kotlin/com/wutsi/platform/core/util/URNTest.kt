package com.wutsi.platform.core.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.text.ParseException

internal class URNTest {
    @Test
    fun `URN with type-name`() {
        val urn = URN.of("event", "key-created")
        assertEquals("urn:event:wutsi:key-created", urn.value)
    }

    @Test
    fun `URN with type-domain-name`() {
        val urn = URN.of("event", "security", "Key-created")
        assertEquals("urn:event:wutsi:security:key-created", urn.value)
    }

    @Test
    fun `parse URN with type-name`() {
        val value = "urn:event:wutsi:key-created"
        val urn = URN.parse(value)
        assertEquals(value, urn.value)
    }

    @Test
    fun `parse URN with type-domain-name`() {
        val value = "URN:EVENT:wutsi:security:key-created"
        val urn = URN.parse(value)
        assertEquals(value.lowercase(), urn.value)
    }

    @Test
    fun `parse URN with invalid prefix`() {
        val value = "xxx:event:wutsi:security:key-created"
        assertThrows<ParseException> {
            URN.parse(value)
        }
    }

    @Test
    fun `parse URN with too many tokens`() {
        val value = "urn:event:wutsi:security:key-created:xxx:yyyy"
        assertThrows<ParseException> {
            URN.parse(value)
        }
    }

    @Test
    fun `parse URN with not enought tokens`() {
        val value = "urn:event"
        assertThrows<ParseException> {
            URN.parse(value)
        }
    }
}
