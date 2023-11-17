package com.wutsi.tracking.manager.service.aggregator.duration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DailyDurationReducerTest {
    private val reducer = DailyDurationReducer()

    @Test
    fun reduce1() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), DurationData("", 10)),
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(0, result.value.value)
    }

    @Test
    fun reduce2() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), DurationData("readstart", 10000)),
            DurationValue(DurationKey("000", "1"), DurationData("readend", 55000))
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(45, result.value.value)
    }

    @Test
    fun reduce2WithOutlier() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), DurationData("readstart", 10000)),
            DurationValue(DurationKey("000", "1"), DurationData("readend", 1000000))
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(300, result.value.value)
    }

    @Test
    fun reduce3() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), DurationData("readstart", 10000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 22000)),
            DurationValue(DurationKey("000", "1"), DurationData("readend", 55000))
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(45, result.value.value)
    }

    @Test
    fun reduce() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), DurationData("readstart", 10000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 15000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 20000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 25000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 30000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 35000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 40000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 45000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 50000)),
            DurationValue(DurationKey("000", "1"), DurationData("readend", 55000)),
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(45, result.value.value)
    }

    @Test
    fun reduceWithOutlier() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), DurationData("readstart", 0)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 20)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 30)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 40)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 50)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 60)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 10000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 15000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 20000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 25000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 30000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 35000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 40000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 45000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 50000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 55000)),
            DurationValue(DurationKey("000", "1"), DurationData("readend", 10000000)),
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(55, result.value.value)
    }

    @Test
    fun noStart() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), DurationData("click", 15000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 20000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 25000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 30000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 35000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 40000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 45000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 50000)),
            DurationValue(DurationKey("000", "1"), DurationData("readend", 55000)),
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(0, result.value.value)
    }

    @Test
    fun noEnd() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), DurationData("start", 15000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 15000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 20000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 25000)),
            DurationValue(DurationKey("000", "1"), DurationData("click", 30000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 35000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 40000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 45000)),
            DurationValue(DurationKey("000", "1"), DurationData("scroll", 50000)),
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(0, result.value.value)
    }
}
