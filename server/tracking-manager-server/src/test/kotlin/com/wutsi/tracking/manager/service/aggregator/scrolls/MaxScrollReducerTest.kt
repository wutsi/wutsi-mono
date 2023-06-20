package com.wutsi.tracking.manager.service.aggregator.scrolls

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MaxScrollReducerTest {
    private val reducer = MaxScrollReducer()

    @Test
    fun reduce() {
        val acc = ScrollValue(ScrollKey("000|11"), 10)
        val cur = ScrollValue(ScrollKey("000|11"), 2)
        val result = reducer.reduce(listOf(acc, cur))

        assertEquals("11", result.key.productId)
        assertEquals(10, result.value)
    }
}
