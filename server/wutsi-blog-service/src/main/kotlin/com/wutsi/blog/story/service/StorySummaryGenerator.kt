package com.wutsi.blog.story.service

import com.wutsi.blog.google.gemini.ai.GHarmCategory
import com.wutsi.blog.google.gemini.ai.GHarmProbability
import com.wutsi.blog.google.gemini.ai.Gemini
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.json.EJSJsonReader
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StorySummaryGenerator(
    private val gemini: Gemini,
    private val editorJSService: EditorJSService,
    private val ejsReader: EJSJsonReader,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StorySummaryGenerator::class.java)
        private const val PROMPT = "Generate a meta description of the following blog post:"
        private val CATEGORY_SEXUAL = listOf(
            GHarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT,
            GHarmCategory.HARM_CATEGORY_SEXUAL,
        )
    }

    fun generate(content: StoryContentEntity, maxLength: Int): Summary? {
        content.content ?: return null

        val doc = ejsReader.read(content.content!!)
        val text = editorJSService.toText(doc)
        if (text.isEmpty()) {
            return null
        }

        val response = gemini.generateContent(
            listOf(PROMPT, text)
        )
        val responseContent = response.candidates.firstOrNull()?.content
        val result = if (responseContent == null) {
            Summary(
                content = defaultSummary(doc, maxLength)
            )
        } else {
            Summary(
                content = responseContent.parts.firstOrNull()?.text,
                sexuallyExplicitContent = response
                    .promptFeedback
                    .safetyRatings
                    .find { rating ->
                        rating.probability == GHarmProbability.HIGH && CATEGORY_SEXUAL.contains(rating.category)
                    } != null
            )
        }

        LOGGER.debug(">>> Content from Story#${content.story.id} - ${content.story.title} - sexually-explicit-content=${result.sexuallyExplicitContent}:\n${result.content}")
        return result
    }

    private fun defaultSummary(doc: EJSDocument, maxLength: Int): String =
        editorJSService.extractSummary(doc, maxLength)
}

class Summary(
    val content: String? = null,
    val sexuallyExplicitContent: Boolean = false,
)
