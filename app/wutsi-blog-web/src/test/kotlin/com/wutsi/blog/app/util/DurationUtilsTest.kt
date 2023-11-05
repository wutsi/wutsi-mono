package com.wutsi.blog.app.util

import org.junit.jupiter.api.Test

class DurationUtilsTest {
    @Test
    fun toHumanReadable() {
        validate("", 0L)
        validate("27s", 27L)
        validate("5m", 300L)
        validate("5m 2s", 302L)
        validate("1h", 3600L)
        validate("1h 5m", 3902L)
        validate("75d", 6480000L)
        validate("75d 11h", 6349626L)
    }

    private fun validate(expected: String, number: Long) {
        kotlin.test.assertEquals(expected, DurationUtils.toHumanReadable(number))
    }
}
