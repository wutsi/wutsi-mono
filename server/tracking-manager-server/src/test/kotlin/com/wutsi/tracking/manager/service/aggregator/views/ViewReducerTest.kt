package com.wutsi.tracking.manager.service.aggregator.views

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ViewReducerTest {
    private val reducer = ViewReducer()

    @Test
    fun reduce() {
        val acc = ViewValue(ViewKey("11"), 10)
        val cur = ViewValue(ViewKey("11"), 1)
        val result = reducer.reduce(listOf(acc, cur))

        assertEquals("11", result.key.productId)
        assertEquals(acc.value + cur.value, result.value)
    }
}
