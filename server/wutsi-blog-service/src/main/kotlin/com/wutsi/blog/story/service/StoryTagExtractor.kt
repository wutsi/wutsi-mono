package com.wutsi.blog.story.service

import com.wutsi.blog.google.gemini.ai.Gemini
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.editorjs.json.EJSJsonReader
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException

@Service
class StoryTagExtractor(
    private val gemini: Gemini,
    private val editorJSService: EditorJSService,
    private val ejsReader: EJSJsonReader,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryTagExtractor::class.java)
        private const val PROMPT = "Can you give me 5 keywords of the following blog post:"
    }

    fun extract(content: StoryContentEntity): List<String> {
        content.content ?: return emptyList()

        val doc = ejsReader.read(content.content!!)
        val text = editorJSService.toText(doc)
        val result = if (text.isEmpty()) {
            emptyList()
        } else {
            try {
                val response = gemini.generateContent(
                    listOf(PROMPT, text)
                )
                val keywords = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text

                keywords?.let {
                    keywords.split("\n")
                        .toList()
                        .map { keyword -> // Format: <index>. <keyword>
                            val i = keyword.indexOf(" ")
                            if (i > 0) keyword.substring(i + 1) else keyword
                        }
                } ?: emptyList()
            } catch (ex: HttpClientErrorException.TooManyRequests) {
                LOGGER.warn("API Quota exhausted", ex)
                emptyList()
            }
        }

        LOGGER.debug(">>> Tags from Story#${content.story.id} - ${content.story.title}:\n$result")
        return result
    }
}
