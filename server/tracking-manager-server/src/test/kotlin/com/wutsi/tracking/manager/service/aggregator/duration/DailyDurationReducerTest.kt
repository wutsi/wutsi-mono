package com.wutsi.tracking.manager.service.aggregator.duration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DailyDurationReducerTest {
    private val reducer = DailyDurationReducer()

    @Test
    fun reduce() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), 10000),
            DurationValue(DurationKey("111", "1"), 55000)
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(45, result.value)
    }

    @Test
    fun noReadEnd() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), 10),
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(0, result.value)
    }
}
