package com.wutsi.ml.document.service

import org.springframework.stereotype.Service

@Service
class StopWordSetProvider {
    private val cache = mutableMapOf<String, StopWordSet>()

    fun get(language: String): StopWordSet {
        if (!cache.containsKey(language)) {
            load(language)
        }
        return cache[language]!!
    }

    private fun load(language: String): StopWordSet {
        val words = StopWordSetProvider::class.java.getResourceAsStream("/stopwords/$language.txt")
            .bufferedReader()
            .lines()
            .toList()
            .filter { !it.isNullOrEmpty() }

        val entry = StopWordSet(words)
        cache[language] = entry
        return entry
    }
}
