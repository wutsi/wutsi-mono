package com.wutsi.application.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class URLBuilderTest {
    @Test
    fun test() {
        val urlBuilder = URLBuilder("https://wwww.google.com")

        assertEquals("https://wwww.google.com/a/b.png", urlBuilder.build("a/b.png"))
        assertEquals("https://wwww.google.com/a/b.png", urlBuilder.build("/a/b.png"))
    }
}
