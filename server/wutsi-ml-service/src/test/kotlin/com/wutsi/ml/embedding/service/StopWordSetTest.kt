package com.wutsi.ml.service

import com.wutsi.ml.document.service.StopWordSet
import org.junit.jupiter.api.Test

internal class StopWordSetTest {
    @Test
    fun contains() {
        val sw = StopWordSet(listOf("this", "is"))
        kotlin.test.assertTrue(sw.contains("This"))
        kotlin.test.assertFalse(sw.contains("car"))
    }

    @Test
    fun size() {
        val sw = StopWordSet(listOf("this", "is"))
        kotlin.test.assertEquals(2, sw.size())
    }
}
