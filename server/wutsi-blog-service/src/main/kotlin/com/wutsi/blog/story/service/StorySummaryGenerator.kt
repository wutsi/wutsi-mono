package com.wutsi.blog.story.service

import com.wutsi.blog.google.gemini.ai.Gemini
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.json.EJSJsonReader
import org.springframework.stereotype.Service

@Service
class StorySummaryGenerator(
    private val gemini: Gemini,
    private val editorJSService: EditorJSService,
    private val ejsReader: EJSJsonReader,
) {
    companion object {
        private val CATEGORY_SEXUAL = listOf("HARM_CATEGORY_SEXUALLY_EXPLICIT", "HARM_CATEGORY_SEXUAL")
    }

    fun generate(content: StoryContentEntity, maxLength: Int): Summary? {
        content.content ?: return null

        val doc = ejsReader.read(content.content!!)
        val text = editorJSService.toText(doc)
        if (text.isEmpty()) {
            return null
        }

        val response = gemini.generateContent(
            listOf(
                "Generate a meta description of this blog post",
                text
            )
        )
        val content = response.candidates.firstOrNull()?.content
        if (content == null) {
            return Summary(
                content = defaultSummary(doc, maxLength)
            )
        }

        return Summary(
            content = content.parts.firstOrNull()?.text,
            sexuallyExplicitContent = response
                .promptFeedback
                .safetyRatings
                .find { rating ->
                    rating.probability == "HIGH" && CATEGORY_SEXUAL.contains(rating.category)
                } != null
        )
    }

    private fun defaultSummary(doc: EJSDocument, maxLength: Int): String =
        editorJSService.extractSummary(doc, maxLength)
}

class Summary(
    val content: String? = null,
    val sexuallyExplicitContent: Boolean = false,
)
