package com.wutsi.recommendation.service

import com.wutsi.recommendation.domain.DocumentEntity
import com.wutsi.recommendation.domain.WordEntity
import org.springframework.stereotype.Service
import java.util.StringTokenizer

@Service
class WordExtractor(
    private val stopWordSetProvider: StopWordSetProvider,
) {
    fun extract(doc: DocumentEntity): List<WordEntity> {
        val text = sanitize(doc.content)
        val stopwords = stopWordSetProvider.get(doc.language)

        val tokenizer = StringTokenizer(text)
        val words = mutableMapOf<String, Long>()
        var wc = 0
        val tokens = mutableSetOf<String>()
        while (tokenizer.hasMoreTokens()) {
            // Extract token
            val token = tokenizer.nextToken()
            if (stopwords.contains(token) || isNumber(token)) {
                continue
            }

            // Increment word frequency
            tokens.add(token)
            if (words.containsKey(token)) {
                words[token] = words[token]!! + 1
            } else {
                words[token] = 1
            }

            // Increment word count
            wc++
        }
        return tokens.map { WordEntity(text = it, tf = words[it]!!.toDouble() / wc.toDouble()) }
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
