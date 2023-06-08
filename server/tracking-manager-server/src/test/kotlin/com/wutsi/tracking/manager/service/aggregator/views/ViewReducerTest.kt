package com.wutsi.tracking.manager.service.aggregator.views

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ViewReducerTest {
    private val reducer = ViewReducer()

    @Test
    fun reduce() {
        val acc = View(ViewKey("11", "1"), 10)
        val cur = View(ViewKey("11", "1"), 1)
        val result = reducer.reduce(acc, cur)

        assertEquals("1", result.key.productId)
        assertEquals("11", result.key.businessId)
        assertEquals(acc.value + cur.value, result.value)
    }
}
