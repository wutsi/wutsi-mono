package com.wutsi.marketplace.access.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HandleGeneratorTest {
    @Test
    fun generate() {
        assertEquals("tv", HandleGenerator.generate("TV"))
        assertEquals("tv-5-pouce", HandleGenerator.generate("Tv 5 pouce"))
        assertEquals("string-with-multiple-spaces", HandleGenerator.generate("string  with multiple Spaces!"))
    }
}
