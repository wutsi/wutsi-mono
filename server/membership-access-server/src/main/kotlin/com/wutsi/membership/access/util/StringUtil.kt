package com.wutsi.membership.access.util

import java.text.Normalizer

object StringUtil {
    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    fun toAscii(str: String): String {
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }
}
