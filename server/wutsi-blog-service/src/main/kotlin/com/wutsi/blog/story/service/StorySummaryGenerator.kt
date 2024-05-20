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
    fun generate(content: StoryContentEntity, maxLength: Int): String? {
        content.content ?: return null

        val doc = ejsReader.read(content.content!!)
        val text = editorJSService.toText(doc)
        val response = gemini.generateContent(
            listOf(
                "Generate a meta description of this blog post",
                text
            )
        )
        return response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: defaultSummary(doc, maxLength)
    }

    private fun defaultSummary(doc: EJSDocument, maxLength: Int): String =
        editorJSService.extractSummary(doc, maxLength)
}
