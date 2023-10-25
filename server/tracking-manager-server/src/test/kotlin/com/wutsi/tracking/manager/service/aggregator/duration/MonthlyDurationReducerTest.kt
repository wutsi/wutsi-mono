package com.wutsi.tracking.manager.service.aggregator.duration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonthlyDurationReducerTest {
    private val reducer = MonthlyDurationReducer()

    @Test
    fun reduce() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), 10),
            DurationValue(DurationKey("111", "1"), 55),
            DurationValue(DurationKey("111", "1"), 5)
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(70, result.value)
    }
}
