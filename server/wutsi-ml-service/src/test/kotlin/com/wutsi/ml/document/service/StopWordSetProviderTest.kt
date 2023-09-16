package com.wutsi.ml.embedding.service

import com.wutsi.ml.document.service.StopWordSetProvider
import org.junit.jupiter.api.Test

internal class StopWordSetProviderTest {
    private val provider = StopWordSetProvider()

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
