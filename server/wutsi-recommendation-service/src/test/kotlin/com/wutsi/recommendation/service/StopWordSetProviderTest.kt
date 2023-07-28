package com.wutsi.recommendation.service

import org.junit.jupiter.api.Test

internal class StopWordsProviderTest{
    private val provider = StopWordsProvider()

    @Test
    fun en() {
        val sw = provider.get("en")
        kotlin.test.assertEquals(1298, sw.size())
    }

    @Test
    fun fr() {
        val sw = provider.get("fr")
        kotlin.test.assertEquals(496, sw.size())
    }
}
