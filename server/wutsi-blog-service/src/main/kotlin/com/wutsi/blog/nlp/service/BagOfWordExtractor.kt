package com.wutsi.blog.nlp.service

import org.springframework.stereotype.Service
import java.util.StringTokenizer

@Service
class BagOfWordExtractor {
    fun extract(text: String, stopwords: StopWords): List<BagOfWordItem> {
        val xtext = sanitize(text)
        val tokenizer = StringTokenizer(xtext)
        val tokens = mutableSetOf<String>()
        val frequency = mutableMapOf<String, Long>()
        var wc = 0
        while (tokenizer.hasMoreTokens()) {
            // Extract token
            val token = tokenizer.nextToken()
            if (stopwords.contains(token) || isNumber(token)) {
                continue
            }
            tokens.add(token)

            // Increment word frequency
            if (frequency.containsKey(token)) {
                frequency[token] = frequency[token]!! + 1
            } else {
                frequency[token] = 1
            }

            // Increment word count
            wc++
        }

        return tokens.map { BagOfWordItem(it, frequency[it]!!.toDouble() / wc) }.sortedByDescending { it.tf }
    }

    private fun isNumber(text: String): Boolean =
        text.toDoubleOrNull() != null

    private fun sanitize(text: String): String =
        text.replace("\\p{Punct}".toRegex(), " ")
            .replace("’", "'")
            .replace("’", "'")
            .replace("”", "")
            .replace("\"", "")
            .lowercase()
}
