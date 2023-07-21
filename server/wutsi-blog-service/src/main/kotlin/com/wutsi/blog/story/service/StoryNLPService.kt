package com.wutsi.blog.story.service

import com.wutsi.blog.nlp.service.BagOfWordExtractor
import com.wutsi.blog.nlp.service.StopWordsProvider
import com.wutsi.blog.nlp.service.Term
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.platform.core.storage.StorageService
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintWriter

@Service
class StoryNLPService(
    private val bowExtractor: BagOfWordExtractor,
    private val stopWordsProvider: StopWordsProvider,
    private val storageService: StorageService,
) {
    fun storeBagOfWords(story: StoryEntity) {
        val lang = story.language ?: "en"
        val stopWords = stopWordsProvider.get(lang)
        val text = toText(story)
        val bow = bowExtractor.extract(text, stopWords)

        val out = ByteArrayOutputStream()
        write(bow, out)
        out.use {
            val path = "stories/${story.id}/bag-of-words.csv"
            storageService.store(path, ByteArrayInputStream(out.toByteArray()), "text/csv", null, "utf-8")
        }
    }

    private fun write(bow: List<Term>, out: OutputStream) {
        val writer = PrintWriter(out)
        writer.use {
            bow.forEach {
                writer.println("${it.text},${it.tf}")
            }
        }
    }

    private fun toText(story: StoryEntity): String {
        val sb = StringBuilder()

        sb.append(story.title).append('\n')
        if (!story.summary.isNullOrEmpty()) {
            sb.append(story.summary)
        }
        return sb.toString()
    }
}
