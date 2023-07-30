package com.wutsi.ml.document.service

class StopWordSet(private val words: List<String>) {
    fun contains(word: String) =
        words.contains(word.lowercase())

    fun size(): Int =
        words.size
}
