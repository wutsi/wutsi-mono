package com.wutsi.tracking.manager.service.aggregator.from

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FromReducerTest {
    private val reducer = FromReducer()

    @Test
    fun reduce() {
        val acc = FromValue(FromKey("1", "read-also"), 10)
        val cur = FromValue(FromKey("1", "read-also"), 1)
        val result = reducer.reduce(listOf(acc, cur))

        assertEquals("1", result.key.productId)
        assertEquals("read-also", result.key.from)
        assertEquals(acc.value + cur.value, result.value)
    }
}
