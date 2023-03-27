package com.wutsi.application.util

import java.text.Normalizer

object StringUtil {
    private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    fun initials(fullName: String?): String {
        val xfullName = fullName?.trim() ?: ""
        if (xfullName.isNullOrEmpty()) {
            return ""
        }

        val index = xfullName.lastIndexOf(' ')
        return if (index > 0) {
            (xfullName.substring(0, 1) + xfullName.substring(index + 1, index + 2)).uppercase()
        } else {
            xfullName.substring(0, 1).uppercase()
        }
    }

    fun capitalize(str: String?): String {
        val buff = StringBuilder()
        var prev: Char = Char.MIN_VALUE
        str?.forEach {
            if (buff.isEmpty() || prev == ' ') {
                buff.append(it.uppercase())
            } else {
                buff.append(it)
            }
            prev = it
        }
        return buff.toString()
    }

    fun capitalizeFirstLetter(str: String?): String =
        if (str.isNullOrEmpty()) {
            ""
        } else {
            str.uppercase().substring(0, 1) + str.substring(1)
        }

    fun firstName(displayName: String?): String {
        if (displayName == null) {
            return ""
        }

        val i = displayName.indexOf(' ')
        return if (i > 0) displayName.substring(0, i).trim() else displayName
    }

    fun lastName(displayName: String?): String {
        if (displayName == null) {
            return ""
        }

        val i = displayName.indexOf(' ')
        return if (i > 0) displayName.substring(i).trim() else ""
    }

    fun unaccent(str: String?): String {
        if (str == null) {
            return ""
        }

        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }
}
