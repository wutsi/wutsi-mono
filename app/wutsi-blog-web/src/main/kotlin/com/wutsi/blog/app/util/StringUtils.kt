package com.wutsi.blog.app.util

import java.text.Normalizer

object StringUtils {
    fun toAscii(string: String?): String {
        if (string.isNullOrEmpty()) {
            return ""
        }

        var str = string.trim()
        val sb = StringBuilder(str.length)
        str = Normalizer.normalize(str, Normalizer.Form.NFD)
        for (c in str.toCharArray()) {
            if (c <= '\u007F') sb.append(c)
        }
        return sb.toString()
    }

    fun toUsername(string: String?): String =
        toAscii(string)
            .replace("\\s".toRegex(), "")
            .lowercase()
}
