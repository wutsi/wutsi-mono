package com.wutsi.blog.nlp.service

class StopWords(private val words: List<String>) {
    fun contains(word: String) =
        words.contains(word.lowercase())

    fun size(): Int =
        words.size
}
