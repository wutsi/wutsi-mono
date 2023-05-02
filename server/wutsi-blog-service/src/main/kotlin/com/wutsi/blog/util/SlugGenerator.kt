package com.wutsi.blog.util

import java.text.Normalizer

object SlugGenerator {
    private val FILTER1 = "\\s+|-+|\\p{Punct}".toRegex()
    private val FILTER2 = "[-*]{2,}".toRegex()
    private val SEPARATOR = "-"

    fun generate(prefix: String, name: String? = null): String {
        var xname = toAscii(name)
            .replace(FILTER1, SEPARATOR)
            .replace(FILTER2, SEPARATOR)
            .lowercase()
        if (xname.endsWith(SEPARATOR)) {
            xname = xname.substring(0, xname.length - 1)
        }
        if (xname.startsWith(SEPARATOR)) {
            xname = xname.substring(1)
        }

        return if (xname.length > 0) "$prefix/$xname" else prefix
    }

    private fun toAscii(string: String?): String {
        if (string == null || string.isEmpty()) {
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
}
