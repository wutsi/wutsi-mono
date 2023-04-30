package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityContext
import com.wutsi.editorjs.readability.RuleResult
import org.jsoup.Jsoup

class ExternalSourceRule: AbstractRule() {
    override fun validate(doc: EJSDocument, context: ReadabilityContext): RuleResult {
        return result(hasQuote(doc) || hasLink(doc))
    }

    private fun hasQuote(doc: EJSDocument) = doc.blocks.find { it.type == BlockType.quote } != null

    private fun hasLink(doc: EJSDocument) = doc.blocks.find { hasLink(it) } != null

    private fun hasLink(block: Block): Boolean {
        if (block.type == BlockType.paragraph){
            return hasLink(block.data.text)
        } else if (block.type == BlockType.list) {
            return hasLink(block.data.items.joinToString { "$it\n" })
        } else if (block.type == BlockType.linkTool || block.type == BlockType.embed) {
            return true
        }
        return false
    }

    private fun hasLink(html: String): Boolean {
        val doc = Jsoup.parse(html)
        return doc.body().select("a").isNotEmpty()
    }
}
