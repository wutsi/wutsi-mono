package com.wutsi.editorjs.utils

import java.util.StringTokenizer

object TextUtils {
    const val SENTENCE_DELIMITERS = ".!?"
    const val WORD_DELIMITERS = " \t\n\r.,;:!?[]{}()-"

    fun sentences(text: String) = tokens (text, SENTENCE_DELIMITERS)

    fun words(text: String) = tokens (text, WORD_DELIMITERS)

    private fun tokens(text: String, delimiters: String): List<String> {
        val toks = StringTokenizer(text, delimiters)
        val result = mutableListOf<String>()
        while (toks.hasMoreTokens()){
            result.add(toks.nextToken().trim())
        }
        return result.filter { it.isNotEmpty() }

    }
}
