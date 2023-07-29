package com.wutsi.recommendation.service

import com.wutsi.recommendation.domain.DocumentEntity
import com.wutsi.recommendation.domain.WordEntity
import org.springframework.stereotype.Service
import java.io.OutputStream
import java.io.OutputStreamWriter

@Service
class TfIdfEmbeddingGenerator(private val extractor: WordExtractor) {
    fun generate(documents: List<DocumentEntity>, out: OutputStream) {
        // Corpus
        val corpus = documents.associate {
            it.id to extractor.extract(it)
        }
        val words = corpus.flatMap { it.value }.map { it.text }.toSet()

        // Compute IDf
        val idf = words.associate { it to computeIdf(it, corpus) }
        corpus.values.forEach { words ->
            words.forEach { word ->
                word.tfIdf = (word.tf ?: 0.0) * (idf[word.text] ?: 0.0)
            }
        }

        // Output
        val writer = OutputStreamWriter(out)
        writer.use {
            // Header: doc-id1,doc-id2,..,doc-idm
            writer.write(documents.map { it.id }.joinToString(separator = ","))
            writer.write("\n")

            // Rows: word:tf-idf1,tf-idf2,..,tf-idfn
            words.forEach { word ->
                writer.write(
                    corpus.keys.map { id -> corpus[id]?.find { it.text == word }?.tfIdf ?: 0.0 }.joinToString(separator = ","),
                )
                writer.write("\n")
            }
        }
    }

    private fun computeIdf(text: String, corpus: Map<Long, List<WordEntity>>): Double {
        val n = corpus.size
        val count = corpus.flatMap { it.value }.filter { it.text == text }.size
        return if (count == 0) {
            0.0
        } else {
            Math.log(n.toDouble() / count)
        }
    }
}
