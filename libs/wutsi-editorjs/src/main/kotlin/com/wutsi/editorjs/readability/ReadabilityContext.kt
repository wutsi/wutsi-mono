package com.wutsi.editorjs.readability

import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.html.tag.TagProvider

data class ReadabilityContext (
        val maxSentencesPerParagraph: Int = 5,
        val maxWordsPerSentence: Int = 25,
        val minParagraphsPerDocument: Int = 3,
        val htmlWriter: EJSHtmlWriter = EJSHtmlWriter(TagProvider())
)
