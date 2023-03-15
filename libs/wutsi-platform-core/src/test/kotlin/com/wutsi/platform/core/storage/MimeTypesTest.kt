package com.wutsi.platform.core.storage

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

internal class MimeTypesTest {
    private val mimes = MimeTypes()

    @ParameterizedTest
    @MethodSource("data")
    fun detect(input: String, expected: String) {
        assertEquals(expected, mimes.detect(input))
    }

    companion object {
        @JvmStatic
        fun data() = listOf(
            Arguments.of("test.png", "image/png"),
            Arguments.of("test.jpg", "image/jpeg"),
            Arguments.of("test.jpeg", "image/jpeg"),
            Arguments.of("test.gif", "image/gif"),
            Arguments.of("test.webp", "image/webp"),
            Arguments.of("test.txt", "text/plain"),
            Arguments.of("test.html", "text/html"),
            Arguments.of("test.pdf", "application/pdf"),
            Arguments.of("keystore/test", "application/octet-stream"),
        )
    }
}
