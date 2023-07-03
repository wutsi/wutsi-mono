package com.wutsi.tracking.manager.service.aggregator.readers

import com.wutsi.tracking.manager.service.aggregator.reader.ReaderKey
import com.wutsi.tracking.manager.service.aggregator.reader.ReaderReducer
import com.wutsi.tracking.manager.service.aggregator.reader.ReaderValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ReaderReducerTest {
    private val reducer = ReaderReducer()

    @Test
    fun reduce() {
        val acc = ReaderValue(ReaderKey("1", "device-1", "11"), 10)
        val cur = ReaderValue(ReaderKey("1", "device-1", "11"), 1)
        val result = reducer.reduce(listOf(acc, cur))

        assertEquals("1", result.key.accountId)
        assertEquals("device-1", result.key.deviceId)
        assertEquals("11", result.key.productId)
        assertEquals(acc.value + cur.value, result.value)
    }
}
