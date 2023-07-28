package com.wutsi.recommendation.service

import com.wutsi.recommendation.domain.DocumentEntity
import com.wutsi.recommendation.domain.TermEntity
import org.springframework.stereotype.Service
import java.awt.SystemColor.text
import java.util.StringTokenizer

@Service
class BagOfWordExtractor(
    private val stopWordSetProvider: StopWordSetProvider,
) {
    fun extract(doc: DocumentEntity): List<TermEntity> {
        val text = sanitize(doc.content)
        val stopwords = stopWordSetProvider.get(doc.language)

        val tokenizer = StringTokenizer(text)
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

        return tokens.map { Term(it, frequency[it]!!.toDouble() / wc) }.sortedByDescending { it.tf }
    }

    private fun isNumber(text: String): Boolean =
        text.toDoubleOrNull() != null

    private fun sanitize(text: String): String =
        text.replace("\\p{Punct}".toRegex(), " ")
            .replace("’", "'")
            .replace("’", "'")
            .replace("”", "")
            .replace("“", "")
            .replace("«", "")
            .replace("»", "")
            .replace("\"", "")
            .lowercase()
}
