package com.wutsi.blog.app.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DurationUtilsTest {

    @Test
    fun toHumanReadable() {
        assertEquals("00:01:00:00", DurationUtils.toHumanReadable(60))
        assertEquals("00:05:50:00", DurationUtils.toHumanReadable(350))
        assertEquals("02:05:30:00", DurationUtils.toHumanReadable(7530))
    }
}
