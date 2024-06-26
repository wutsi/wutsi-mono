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

            Arguments.of("test.json", "application/json"),
            Arguments.of("test.pdf", "application/pdf"),
            Arguments.of("test.json", "application/json"),
            Arguments.of("keystore/test.cbz", "application/x-cdisplay"),
            Arguments.of("keystore/test.epub", "application/epub+zip"),
            Arguments.of("keystore/test.doc", "application/msword"),
            Arguments.of("keystore/test.docx", "application/vnd.openxmlformat"),
            Arguments.of("keystore/test.ppt", "application/vnd.ms-powerpoint"),
            Arguments.of("keystore/test.pptx", "application/vnd.ms-powerpoint"),
            Arguments.of("keystore/test.xls", "application/vnd.ms-excel"),
            Arguments.of("keystore/test.xlsx", "application/vnd.ms-excel"),
            Arguments.of("keystore/test.rar", "application/x-rar-compressed"),
            Arguments.of("keystore/test.zip", "application/x-zip"),

            Arguments.of("test.txt", "text/plain"),
            Arguments.of("test.html", "text/html"),
            Arguments.of("test.htm", "text/html"),
            Arguments.of("test.xml", "text/xml"),

            Arguments.of("keystore/test", "application/octet-stream"),
        )
    }
}
