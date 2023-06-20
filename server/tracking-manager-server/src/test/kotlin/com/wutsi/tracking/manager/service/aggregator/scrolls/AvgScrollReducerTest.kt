package com.wutsi.tracking.manager.service.aggregator.scrolls

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AvgScrollReducerTest {
    private val reducer = AvgScrollReducer()

    @Test
    fun reduce() {
        val acc = ScrollValue(ScrollKey("11"), 10)
        val cur = ScrollValue(ScrollKey("11"), 2)
        val result = reducer.reduce(listOf(acc, cur))

        assertEquals("11", result.key.productId)
        assertEquals(6, result.value)
    }
}
