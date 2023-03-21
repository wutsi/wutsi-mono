package com.wutsi.membership.access.util

object AccountHandleUtil {
    fun generate(displayName: String, maxLength: Int): String {
        val handle = displayName.lowercase().take(maxLength)
            .filter { accept(it) }
        return StringUtil.toAscii(handle)
    }

    private fun accept(char: Char): Boolean =
        char.isLetterOrDigit()
}
