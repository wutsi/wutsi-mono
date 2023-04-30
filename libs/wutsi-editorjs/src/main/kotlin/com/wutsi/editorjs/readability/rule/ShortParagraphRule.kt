package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityContext
import com.wutsi.editorjs.readability.RuleResult
import com.wutsi.editorjs.utils.TextUtils
import org.jsoup.Jsoup

class ShortParagraphRule: AbstractRule() {
    override fun validate(doc: EJSDocument, context: ReadabilityContext): RuleResult {
        val paragraphs = getAllParagraphs(doc)
        if (paragraphs.isEmpty()) {
            return result(false)
        }

        val xparagraphs = getLongParagraphs(doc, context)
        val score = 100 - (100*xparagraphs.size)/paragraphs.size
        return result(score)
    }

    private fun getAllParagraphs(doc: EJSDocument) = doc.blocks.filter { it.type == BlockType.paragraph }

    private fun getLongParagraphs(doc: EJSDocument, context: ReadabilityContext) = doc.blocks.filter { it.type == BlockType.paragraph && isLong(it, context) }

    private fun isLong(block: Block, context: ReadabilityContext): Boolean {
        val text = Jsoup.parse(block.data.text).text()
        val sentences = TextUtils.sentences(text)

        return sentences.size > context.maxSentencesPerParagraph
    }
}
