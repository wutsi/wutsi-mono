package com.wutsi.blog.story.service

import com.wutsi.blog.nlp.service.BagOfWordExtractor
import com.wutsi.blog.nlp.service.BagOfWordItem
import com.wutsi.blog.nlp.service.StopWordsProvider
import com.wutsi.blog.story.domain.StoryContentEntity
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
    private val ejsService: EditorJSService,
    private val storageService: StorageService,
) {
    fun storeBagOfWords(story: StoryEntity, storyContent: StoryContentEntity?) {
        val lang = story.language ?: "en"
        val stopWords = stopWordsProvider.get(lang)
        val text = toText(story, storyContent)
        val bow = bowExtractor.extract(text, stopWords)

        val out = ByteArrayOutputStream()
        write(bow, out)
        out.use {
            val path = "stories/${story.id}/bag-of-words.csv"
            storageService.store(path, ByteArrayInputStream(out.toByteArray()), "text/csv", null, "utf-8")
        }
    }

    private fun write(bow: List<BagOfWordItem>, out: OutputStream) {
        val writer = PrintWriter(out)
        writer.use {
            bow.forEach {
                writer.println("${it.text},${it.tf}")
            }
        }
    }

    private fun toText(story: StoryEntity, storyContent: StoryContentEntity?): String {
        val sb = StringBuilder()
        val content = storyContent?.content

        sb.append(story.title).append('\n')
        if (content == null) {
            if (!story.summary.isNullOrEmpty()) {
                sb.append(story.summary)
            }
        } else {
            val doc = ejsService.fromJson(content)
            sb.append(ejsService.toText(doc))
        }
        return sb.toString()
    }
}
