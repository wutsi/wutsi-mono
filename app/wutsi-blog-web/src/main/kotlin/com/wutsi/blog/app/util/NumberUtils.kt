package com.wutsi.blog.app.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.StringCharacterIterator

object NumberUtils {
    fun toHumanReadable(
        value: Long,
        fmt: NumberFormat = DecimalFormat("#.#"),
        suffix: String = ""
    ): String {
        var bytes = value
        if (bytes == 0L) {
            return "0 $suffix"
        } else if (-1000 < bytes && bytes < 1000) {
            return "$bytes $suffix"
        }
        val ci = StringCharacterIterator("KMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }

        return fmt.format(bytes / 1000.0) + " " + ci.current() + suffix
    }
}
