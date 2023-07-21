package com.wutsi.blog.nlp.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class StopWordsProviderTest {
    private val provider = StopWordsProvider()

    @Test
    fun en() {
        val sw = provider.get("en")
        assertEquals(1298, sw.size())
    }

    @Test
    fun fr() {
        val sw = provider.get("fr")
        assertEquals(496, sw.size())
    }
}
