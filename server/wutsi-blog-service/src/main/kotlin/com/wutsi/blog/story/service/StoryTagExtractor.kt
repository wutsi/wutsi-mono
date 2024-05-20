package com.wutsi.blog.story.service

import com.wutsi.blog.google.gemini.ai.Gemini
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.editorjs.json.EJSJsonReader
import org.springframework.stereotype.Service

@Service
class StoryTagExtractor(
    private val gemini: Gemini,
    private val editorJSService: EditorJSService,
    private val ejsReader: EJSJsonReader,
) {
    fun extract(content: StoryContentEntity): List<String> {
        content.content ?: return emptyList()

        val doc = ejsReader.read(content.content!!)
        val text = editorJSService.toText(doc)
        val response = gemini.generateContent(
            listOf(
                "Can you give me 5 keywords of this blog post",
                text
            )
        )
        val keywords = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
        return keywords?.let {
            keywords.split("\n")
                .toList()
                .map { keyword -> // Format: <index>. <keyword>
                    val i = keyword.indexOf(" ")
                    if (i > 0) keyword.substring(i + 1) else keyword
                }
        } ?: emptyList()
    }
}
