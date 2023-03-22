package com.wutsi.marketplace.manager.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ProductHandleGeneratorTest {
    @Test
    fun generate() {
        assertEquals("this-is-a-slug", ProductHandleGenerator.generate("This is a slug"))
    }

    @Test
    fun filterDash() {
        assertEquals("this-is-a-slug", ProductHandleGenerator.generate("This-is a slug"))
    }

    @Test
    fun filterMultipleDash() {
        assertEquals("this-is-a-slug", ProductHandleGenerator.generate("This-is a ,slug"))
    }

    @Test
    fun filterPuctuation() {
        assertEquals("this-is-a-slug", ProductHandleGenerator.generate("This.is!a,slug"))
    }

    @Test
    fun filterTrailingSeparator() {
        assertEquals("this-is-a-slug", ProductHandleGenerator.generate("This is a slug?"))
    }
}
