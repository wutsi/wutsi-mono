package com.wutsi.blog.nlp.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class StopWordsTest {
    @Test
    fun contains() {
        val sw = StopWords(listOf("this", "is"))
        assertTrue(sw.contains("This"))
        assertFalse(sw.contains("car"))
    }

    @Test
    fun size() {
        val sw = StopWords(listOf("this", "is"))
        assertEquals(2, sw.size())
    }
}
