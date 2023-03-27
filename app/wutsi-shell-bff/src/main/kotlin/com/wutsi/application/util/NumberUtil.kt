package com.wutsi.application.util

import java.text.CharacterIterator
import java.text.StringCharacterIterator

object NumberUtil {
    fun toHumanReadableByteCountSI(bytes: Long): String {
        var value = bytes
        if (-1000 < value && value < 1000) {
            return "$value B"
        }
        val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
        while (value <= -999950 || value >= 999950) {
            value /= 1000
            ci.next()
        }
        return String.format("%.1f %cB", value / 1000.0, ci.current())
    }
}
