package com.wutsi.recommendation.service

import org.springframework.stereotype.Service
import kotlin.streams.toList

@Service
class StopWordsProvider {
    private val cache = mutableMapOf<String, StopWords>()

    fun get(language: String): StopWords {
        if (!cache.containsKey(language)) {
            load(language)
        }
        return cache[language]!!
    }

    private fun load(language: String): StopWords {
        val words = StopWordsProvider::class.java.getResourceAsStream("/stopwords/$language.txt")
            ?.bufferedReader()
            ?.lines()
            ?.toList()
            ?.filter { !it.isNullOrEmpty() }
            ?: emptyList()

        val entry = StopWords(words)
        cache[language] = entry
        return entry
    }
}
