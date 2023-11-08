package com.wutsi.tracking.manager.service.aggregator.click

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ClickReducerTest {
    private val reducer = ClickReducer()

    @Test
    fun reduce() {
        val acc = ClickValue(ClickKey("11", "device-1", "1"), 10)
        val cur = ClickValue(ClickKey("11", "device-1", "1"), 1)
        val result = reducer.reduce(listOf(acc, cur))

        assertEquals("1", result.key.productId)
        assertEquals("device-1", result.key.deviceId)
        assertEquals("11", result.key.accountId)
        assertEquals(acc.value + cur.value, result.value)
    }
}
