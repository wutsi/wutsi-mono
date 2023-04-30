package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityContext
import com.wutsi.editorjs.readability.RuleResult

class MinParagraphRule: AbstractRule() {
    override fun validate(doc: EJSDocument, context: ReadabilityContext): RuleResult {
        val paragraphs = getAllParagraphs(doc)
        return result(paragraphs.size > context.minParagraphsPerDocument)
    }

    private fun getAllParagraphs(doc: EJSDocument) = doc.blocks.filter { it.type == BlockType.paragraph }
}
