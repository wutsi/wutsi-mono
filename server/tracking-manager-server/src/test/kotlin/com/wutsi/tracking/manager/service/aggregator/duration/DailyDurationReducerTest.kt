package com.wutsi.tracking.manager.service.aggregator.duration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DailyDurationReducerTest {
    private val reducer = DailyDurationReducer()

    @Test
    fun reduce1() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), 10),
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(0, result.value)
    }

    @Test
    fun reduce2() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), 10000),
            DurationValue(DurationKey("000", "1"), 55000)
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(45, result.value)
    }

    @Test
    fun reduce2WithOutlier() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), 10000),
            DurationValue(DurationKey("000", "1"), 1000000)
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(60, result.value)
    }

    @Test
    fun reduce3() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), 10000),
            DurationValue(DurationKey("000", "1"), 22000),
            DurationValue(DurationKey("000", "1"), 55000)
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(45, result.value)
    }

    @Test
    fun reduce() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), 10000),
            DurationValue(DurationKey("000", "1"), 15000),
            DurationValue(DurationKey("000", "1"), 20000),
            DurationValue(DurationKey("000", "1"), 25000),
            DurationValue(DurationKey("000", "1"), 30000),
            DurationValue(DurationKey("000", "1"), 35000),
            DurationValue(DurationKey("000", "1"), 40000),
            DurationValue(DurationKey("000", "1"), 45000),
            DurationValue(DurationKey("000", "1"), 50000),
            DurationValue(DurationKey("000", "1"), 55000),
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(45, result.value)
    }

    @Test
    fun reduceWithOutlier() {
        val list = listOf(
            DurationValue(DurationKey("000", "1"), 0),
            DurationValue(DurationKey("000", "1"), 20),
            DurationValue(DurationKey("000", "1"), 30),
            DurationValue(DurationKey("000", "1"), 40),
            DurationValue(DurationKey("000", "1"), 50),
            DurationValue(DurationKey("000", "1"), 60),
            DurationValue(DurationKey("000", "1"), 10000),
            DurationValue(DurationKey("000", "1"), 15000),
            DurationValue(DurationKey("000", "1"), 20000),
            DurationValue(DurationKey("000", "1"), 25000),
            DurationValue(DurationKey("000", "1"), 30000),
            DurationValue(DurationKey("000", "1"), 35000),
            DurationValue(DurationKey("000", "1"), 40000),
            DurationValue(DurationKey("000", "1"), 45000),
            DurationValue(DurationKey("000", "1"), 50000),
            DurationValue(DurationKey("000", "1"), 55000),
            DurationValue(DurationKey("000", "1"), 10000000),
        )
        val result = reducer.reduce(list)

        assertEquals("000", result.key.correlationId)
        assertEquals("1", result.key.productId)
        assertEquals(55, result.value)
    }
}
