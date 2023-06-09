package com.wutsi.tracking.manager.service.aggregator.reads

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ReadReducerTest {
    private val reducer = ReadReducer()

    @Test
    fun reduce() {
        val acc = Read(ReadKey("11"), 10)
        val cur = Read(ReadKey("11"), 1)
        val result = reducer.reduce(acc, cur)

        assertEquals("11", result.key.productId)
        assertEquals(acc.value + cur.value, result.value)
    }
}
